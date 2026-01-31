package org.sake_hack.ui.stocklist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.compose.viewmodel.koinViewModel
import org.sake_hack.core.common.error.AppError
import org.sake_hack.core.common.error.toUserMessage
import org.sake_hack.feature.stocklist.presentation.StockListIntent
import org.sake_hack.feature.stocklist.presentation.StockListUiState
import org.sake_hack.feature.stocklist.presentation.StockListViewModel
import org.sake_hack.ui.components.CommonAppBar
import org.sake_hack.ui.stocklist.components.*

/**
 * 在庫一覧画面
 */
@Composable
fun StockListScreen(
    onNavigate: (String) -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StockListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    StockListContent(
        onMenuClick = onMenuClick,
        uiState = uiState,
        onIntent = viewModel::handleIntent,
        onNavigate = onNavigate,
        modifier = modifier
    )
}

/**
 * 在庫一覧コンテンツ: Scaffoldレイアウト
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StockListContent(
    onMenuClick: () -> Unit,
    uiState: StockListUiState,
    onIntent: (StockListIntent) -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 画像選択Launcher
    val imagePickerLauncher: ImagePickerLauncher = rememberImagePickerLauncher(
        onImageSelected = { imageData ->
            onIntent(StockListIntent.OnImageSelected(imageData))
        },
        onError = { error ->
            onIntent(StockListIntent.OnImagePickerError(error))
        }
    )

    // selectedImageSourceが設定されたらLauncherを起動
    LaunchedEffect(uiState.selectedImageSource) {
        uiState.selectedImageSource?.let { source ->
            imagePickerLauncher.launch(source)
        }
    }

    Scaffold(
        topBar = {
            Column {
                CommonAppBar(
                    title = "在庫一覧",
                    onMenuClick = onMenuClick
                )
                // ActionBar - フィルター・ソートボタン
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    StockActionBar(
                        filterCriteria = uiState.filterCriteria,
                        sortCriteria = uiState.sortCriteria,
                        onFilterClick = { onIntent(StockListIntent.OpenFilterDialog) },
                        onSortClick = { onIntent(StockListIntent.OpenSortMenu) }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIntent(StockListIntent.OpenCreateDialog) },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "在庫を追加"
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        StockListMainContent(
            uiState = uiState,
            onIntent = onIntent,
            modifier = Modifier.padding(paddingValues)
        )
    }

    // ダイアログ・メニュー
    StockListDialogs(
        uiState = uiState,
        onIntent = onIntent
    )

    // Snackbar表示
    StockListSnackbars(
        uiState = uiState,
        onIntent = onIntent
    )
}

/**
 * メインコンテンツ: 状態に応じた表示切り替え
 */
@Composable
private fun StockListMainContent(
    uiState: StockListUiState,
    onIntent: (StockListIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            // 初回ローディング
            uiState.isInitialLoading -> {
                StockListLoadingState()
            }

            // エラー状態
            uiState.error != null && uiState.displayedStocks.isEmpty() -> {
                uiState.error?.let { error ->
                    StockListErrorState(
                        error = error,
                        onRetry = { onIntent(StockListIntent.Refresh) }
                    )
                }
            }

            // 空状態(フィルター適用後)
            uiState.displayedStocks.isEmpty() && uiState.filterCriteria.isActive() -> {
                StockListEmptyState(
                    message = "フィルター条件に一致する在庫がありません",
                    onClearFilter = { onIntent(StockListIntent.ClearFilter) }
                )
            }

            // 通常状態
            else -> {
                StockList(
                    uiState = uiState,
                    onIntent = onIntent
                )
            }
        }
    }
}

/**
 * 在庫リスト
 */
@Composable
private fun StockList(
    uiState: StockListUiState,
    onIntent: (StockListIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // 無限スクロールの実装
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null) {
                    val totalItems = uiState.displayedStocks.size
                    // 最後から3番目でプリフェッチ
                    if (lastVisibleIndex >= totalItems - 3 && uiState.hasNextPage() && !uiState.isLoadingMore) {
                        onIntent(StockListIntent.LoadNextPage)
                    }
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8FB)), // デザイン仕様: $sumi-50
        contentPadding = PaddingValues(
            top = 12.dp,
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 在庫リスト
        items(
            items = uiState.displayedStocks,
            key = { it.id }
        ) { stock ->
            StockListItem(
                stock = stock,
                onClick = { onIntent(StockListIntent.OpenStockDetail(stock)) }
            )
        }

        // ローディングインジケーター
        if (uiState.isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // インラインエラーバナー
        if (uiState.error != null && uiState.displayedStocks.isNotEmpty()) {
            item {
                uiState.error?.let { error ->
                    StockListInlineErrorBanner(
                        error = error,
                        onRetry = { onIntent(StockListIntent.LoadNextPage) },
                        onDismiss = { onIntent(StockListIntent.Refresh) }
                    )
                }
            }
        }
    }
}

/**
 * ローディング状態
 */
@Composable
private fun StockListLoadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * エラー状態(フルスクリーン)
 * デザイン仕様: stock_list.pen - 画面1
 */
@Composable
private fun StockListErrorState(
    error: AppError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // アイコン円形背景
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CloudOff,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // テキストコンテナ
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = getErrorTitle(error),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = error.toUserMessage(),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 再試行ボタン
        if (error.isRetryable) {
            Button(
                onClick = onRetry,
                modifier = Modifier.height(40.dp)
            ) {
                Text("再試行")
            }
        }
    }
}

/**
 * 空状態
 */
@Composable
private fun StockListEmptyState(
    message: String,
    onClearFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(onClick = onClearFilter) {
            Text("フィルターをクリア")
        }
    }
}

/**
 * ダイアログ・メニュー
 */
@Composable
private fun StockListDialogs(
    uiState: StockListUiState,
    onIntent: (StockListIntent) -> Unit
) {
    // フィルターダイアログ
    if (uiState.isFilterDialogOpen) {
        StockFilterDialog(
            filterCriteria = uiState.tempFilterCriteria,
            onUpdateName = { onIntent(StockListIntent.UpdateNameFilter(it)) },
            onToggleCategory = { onIntent(StockListIntent.ToggleCategoryFilter(it)) },
            onUpdateRegion = { onIntent(StockListIntent.UpdateRegionFilter(it)) },
            onApply = { onIntent(StockListIntent.ApplyFilter) },
            onDismiss = { onIntent(StockListIntent.CloseFilterDialog) }
        )
    }

    // ソートメニュー
    if (uiState.isSortMenuOpen) {
        StockSortMenu(
            currentSortCriteria = uiState.sortCriteria,
            onSortSelected = { onIntent(StockListIntent.ApplySort(it)) },
            onDismiss = { onIntent(StockListIntent.CloseSortMenu) }
        )
    }

    // 詳細ダイアログ
    if (uiState.isDetailDialogOpen) {
        uiState.selectedStock?.let { stock ->
            StockDetailDialog(
                stock = stock,
                onDismiss = { onIntent(StockListIntent.CloseDetailDialog) },
                onEdit = { onIntent(StockListIntent.StartEdit) }
            )
        }
    }

    // 編集ダイアログ(クロップモード時は非表示)
    if (uiState.isEditMode && !uiState.isCropMode) {
        uiState.editingStock?.let { editingStock ->
            StockEditDialog(
                editingStock = editingStock,
                validationErrors = uiState.validationErrors,
                isSaving = uiState.isSaving,
                onUpdateField = { update -> onIntent(StockListIntent.UpdateEditField(update)) },
                onSave = { onIntent(StockListIntent.SaveEdit) },
                onCancel = { onIntent(StockListIntent.CancelEdit) },
                onImageClick = { onIntent(StockListIntent.OpenImagePicker) }
            )
        }
    }

    // 追加ダイアログ(クロップモード時は非表示)
    if (uiState.isCreateDialogOpen && !uiState.isCropMode) {
        uiState.creatingStock?.let { creatingStock ->
            StockCreateDialog(
                creatingStock = creatingStock,
                validationErrors = uiState.createValidationErrors,
                isCreating = uiState.isCreating,
                onUpdateField = { update -> onIntent(StockListIntent.UpdateCreateField(update)) },
                onSave = { onIntent(StockListIntent.SaveCreate) },
                onCancel = { onIntent(StockListIntent.CancelCreate) },
                onImageClick = { onIntent(StockListIntent.OpenImagePicker) }
            )
        }
    }

    // 画像ピッカーBottomSheet
    if (uiState.isImagePickerOpen) {
        ImagePickerBottomSheet(
            onSelectSource = { source -> onIntent(StockListIntent.SelectImageSource(source)) },
            onDismiss = { onIntent(StockListIntent.CloseImagePicker) }
        )
    }

    // 画像トリミング画面
    if (uiState.isCropMode) {
        uiState.selectedImageData?.let { imageData ->
            ImageCropScreen(
                imageData = imageData,
                onCropComplete = { croppedData -> onIntent(StockListIntent.CropImage(croppedData)) },
                onCancel = { onIntent(StockListIntent.CancelCrop) }
            )
        }
    }
}

/**
 * Snackbar表示
 */
@Composable
private fun StockListSnackbars(
    uiState: StockListUiState,
    onIntent: (StockListIntent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // 成功メッセージ
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onIntent(StockListIntent.Refresh)
        }
    }

    SnackbarHost(hostState = snackbarHostState)
}

/**
 * パターン2: ページネーションエラー(インラインバナー)
 * デザイン仕様: stock_list.pen - 画面3
 */
@Composable
private fun StockListInlineErrorBanner(
    error: AppError,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.error,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // エラーテキスト行
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.CloudOff,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = "読み込みに失敗しました",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.error
            )
        }

        // 再試行ボタン
        if (error.isRetryable) {
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("再試行")
            }
        }
    }
}

private fun getErrorTitle(error: AppError): String {
    return when (error) {
        is AppError.Network -> "接続エラー"
        is AppError.Http -> when (error.statusCode) {
            404 -> "データが見つかりません"
            else -> "エラーが発生しました"
        }
        is AppError.Parse -> "データエラー"
        is AppError.Api -> "エラーが発生しました"
        is AppError.Unknown -> "エラーが発生しました"
    }
}
