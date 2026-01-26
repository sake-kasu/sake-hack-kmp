package org.sake_hack.ui.stocklist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.compose.viewmodel.koinViewModel
import org.sake_hack.feature.stocklist.presentation.StockListIntent
import org.sake_hack.feature.stocklist.presentation.StockListUiState
import org.sake_hack.feature.stocklist.presentation.StockListViewModel
import org.sake_hack.ui.stocklist.components.*

/**
 * 在庫一覧画面
 */
@Composable
fun StockListScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StockListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    StockListContent(
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
    uiState: StockListUiState,
    onIntent: (StockListIntent) -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            StockListTopBar(
                filterCount = uiState.filterCriteria.activeFilterCount(),
                onFilterClick = { onIntent(StockListIntent.OpenFilterDialog) },
                onSortClick = { onIntent(StockListIntent.OpenSortMenu) }
            )
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
                uiState.error?.let { errorMessage ->
                    StockListErrorState(
                        error = errorMessage,
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
        contentPadding = PaddingValues(
            top = 12.dp,
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        // アクションバー(フィルター・ソート)
        item {
            StockActionBar(
                filterCriteria = uiState.filterCriteria,
                sortCriteria = uiState.sortCriteria,
                onFilterClick = { onIntent(StockListIntent.OpenFilterDialog) },
                onSortClick = { onIntent(StockListIntent.OpenSortMenu) }
            )
        }

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
                uiState.error?.let { errorMessage ->
                    StockListErrorBanner(
                        error = errorMessage,
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
 * エラー状態
 */
@Composable
private fun StockListErrorState(
    error: String,
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
        Icon(
            imageVector = Icons.Outlined.CloudOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "在庫の取得に失敗しました",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRetry) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("再試行")
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

    // 編集ダイアログ
    if (uiState.isEditMode) {
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

    // 追加ダイアログ
    if (uiState.isCreateDialogOpen) {
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
