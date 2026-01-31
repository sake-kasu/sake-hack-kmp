package org.sake_hack.feature.sakelist.presentation

import org.sake_hack.core.common.error.AppError
import org.sake_hack.feature.sakelist.domain.model.FilterCriteria
import org.sake_hack.feature.sakelist.domain.model.Sake
import org.sake_hack.feature.sakelist.domain.model.SortOption

/**
 * 酒一覧画面のUI状態（MVIパターン）
 * 画面全体の状態を表す単一のイミュータブルな状態
 */
data class SakeListUiState(
    // ページネーション対応: 表示中のアイテムリスト
    val displayedSake: List<Sake> = emptyList(),

    // ローディング状態の分離
    val isInitialLoading: Boolean = false,  // 初期ロード時のみtrue
    val isLoadingMore: Boolean = false,     // 追加ロード時(重複リクエスト防止用、UI非表示)

    // ページング状態
    val currentOffset: Int = 0,
    val limit: Int = 20,
    val totalCount: Long = 0,

    // エラー状態
    val error: AppError? = null,

    // フィルター状態
    val filterCriteria: FilterCriteria = FilterCriteria(),
    val isFilterDialogOpen: Boolean = false,

    // 並べ替え状態
    val sortOption: SortOption = SortOption.DEFAULT,
    val isSortMenuOpen: Boolean = false,

    // 詳細状態
    val selectedSake: Sake? = null,
    val isDetailDialogOpen: Boolean = false
) {
    /**
     * 次のページが存在するかどうか
     */
    fun hasNextPage(): Boolean = currentOffset + displayedSake.size < totalCount
}
