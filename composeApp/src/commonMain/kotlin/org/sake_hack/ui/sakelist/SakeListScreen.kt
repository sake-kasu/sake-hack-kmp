package org.sake_hack.ui.sakelist

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
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.compose.viewmodel.koinViewModel
import org.sake_hack.core.common.error.AppError
import org.sake_hack.core.common.error.toUserMessage
import org.sake_hack.feature.sakelist.presentation.SakeListIntent
import org.sake_hack.feature.sakelist.presentation.SakeListUiState
import org.sake_hack.feature.sakelist.presentation.SakeListViewModel
import org.sake_hack.ui.components.CommonAppBar
import org.sake_hack.ui.sakelist.components.*

@Composable
fun SakeListScreen(
    onNavigate: (String) -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SakeListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SakeListContent(
        uiState = uiState,
        onIntent = viewModel::handleIntent,
        onMenuClick = onMenuClick,
        onNavigate = onNavigate,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SakeListContent(
    uiState: SakeListUiState,
    onIntent: (SakeListIntent) -> Unit,
    onMenuClick: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            Column {
                CommonAppBar(
                    title = "酒一覧",
                    onMenuClick = onMenuClick
                )
                // ActionBar - フィルターボタン
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val filterCount = uiState.filterCriteria.activeFilterCount()
                    OutlinedButton(
                        onClick = { onIntent(SakeListIntent.OpenFilterDialog) },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "フィルター",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        if (filterCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Badge {
                                Text(
                                    text = filterCount.toString(),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        SakeListMainContent(
            uiState = uiState,
            onIntent = onIntent,
            modifier = Modifier.padding(paddingValues)
        )
    }

    SakeListDialogs(
        uiState = uiState,
        onIntent = onIntent
    )
}

/**
 * メインコンテンツ: 状態に応じた表示切り替え
 */
@Composable
private fun SakeListMainContent(
    uiState: SakeListUiState,
    onIntent: (SakeListIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            uiState.isInitialLoading -> {
                LoadingIndicator(modifier = Modifier.align(Alignment.Center))
            }

            uiState.error != null && uiState.displayedSake.isEmpty() -> {
                uiState.error?.let { error ->
                    FullScreenErrorView(
                        error = error,
                        onRetry = { onIntent(SakeListIntent.LoadSakeList) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            uiState.displayedSake.isEmpty() -> {
                EmptyView(
                    isFilterActive = uiState.filterCriteria.isActive(),
                    onClearFilter = { onIntent(SakeListIntent.ClearFilter) },
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                InfiniteScrollSakeList(
                    sakeList = uiState.displayedSake,
                    hasNextPage = uiState.hasNextPage(),
                    error = uiState.error,
                    onSakeClick = { onIntent(SakeListIntent.OpenSakeDetail(it)) },
                    onLoadMore = { onIntent(SakeListIntent.LoadNextPage) },
                    onRetry = { onIntent(SakeListIntent.LoadNextPage) }
                )
            }
        }
    }
}

/**
 * ダイアログ表示
 */
@Composable
private fun SakeListDialogs(
    uiState: SakeListUiState,
    onIntent: (SakeListIntent) -> Unit
) {
    if (uiState.isFilterDialogOpen) {
        FilterDialog(
            filterCriteria = uiState.filterCriteria,
            onIntent = onIntent
        )
    }

    if (uiState.isDetailDialogOpen) {
        uiState.selectedSake?.let { sake ->
            SakeDetailDialog(
                sake = sake,
                onDismiss = { onIntent(SakeListIntent.CloseDetailDialog) }
            )
        }
    }
}

@Composable
private fun InfiniteScrollSakeList(
    sakeList: List<org.sake_hack.feature.sakelist.domain.model.Sake>,
    hasNextPage: Boolean,
    error: AppError?,
    onSakeClick: (org.sake_hack.feature.sakelist.domain.model.Sake) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // スクロール監視: 最後のアイテムに到達したら次ページをロード
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                // 最後から3番目のアイテムが表示されたらプリフェッチ
                // エラーがない場合のみロード
                if (error == null && hasNextPage && lastVisibleIndex != null && lastVisibleIndex >= sakeList.size - 3) {
                    onLoadMore()
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
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
        items(sakeList, key = { it.id }) { sake ->
            SakeListItem(
                sake = sake,
                onClick = { onSakeClick(sake) }
            )
        }

        // ページネーションエラー(データあり + 追加ロードエラー)
        error?.let { appError ->
            if (sakeList.isNotEmpty()) {
                item {
                    InlineErrorBanner(
                        error = appError,
                        onRetry = onRetry
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier)
}

/**
 * パターン1: 初期ロードエラー(画面中央)
 * デザイン仕様: sake_list.pen - 画面1
 */
@Composable
private fun FullScreenErrorView(
    error: AppError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
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
 * パターン2: ページネーションエラー(インラインバナー)
 * デザイン仕様: sake_list.pen - 画面3
 */
@Composable
private fun InlineErrorBanner(
    error: AppError,
    onRetry: () -> Unit,
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
                imageVector = Icons.Outlined.ErrorOutline,
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

/**
 * 検索結果0件の表示
 * デザイン仕様: sake_list.pen - 画面3
 * パターン1: フィルター無効時 → メッセージのみ
 * パターン2: フィルター有効時 → フィルタークリアボタン表示
 */
@Composable
private fun EmptyView(
    isFilterActive: Boolean,
    onClearFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // アイコン円形背景
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // テキストコンテナ
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "該当する酒が見つかりません",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "検索条件を変更してもう一度お試しください",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 1.6.em
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        // フィルタークリアボタン(フィルター有効時のみ表示)
        if (isFilterActive) {
            OutlinedButton(
                onClick = onClearFilter,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Text("フィルターをクリア")
            }
        }
    }
}
