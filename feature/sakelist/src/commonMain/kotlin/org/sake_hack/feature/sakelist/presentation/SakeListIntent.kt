package org.sake_hack.feature.sakelist.presentation

import org.sake_hack.feature.sakelist.domain.model.Sake
import org.sake_hack.feature.sakelist.domain.model.SortOption

/**
 * 酒一覧画面のユーザーインテント（MVIパターン）
 */
sealed interface SakeListIntent {
    // データ読み込み
    data object LoadSakeList : SakeListIntent
    data object LoadNextPage : SakeListIntent
    data object Refresh : SakeListIntent

    // フィルター
    data object OpenFilterDialog : SakeListIntent
    data object CloseFilterDialog : SakeListIntent
    data class UpdateSakeNameFilter(val sakeName: String?) : SakeListIntent
    data class ToggleSakeTypeFilter(val sakeType: String) : SakeListIntent
    data class UpdateRegionFilter(val region: String?) : SakeListIntent
    data object ApplyFilter : SakeListIntent
    data object ClearFilter : SakeListIntent

    // 並べ替え
    data object OpenSortMenu : SakeListIntent
    data object CloseSortMenu : SakeListIntent
    data class SelectSort(val sortOption: SortOption) : SakeListIntent

    // 詳細
    data class OpenSakeDetail(val sake: Sake) : SakeListIntent
    data object CloseDetailDialog : SakeListIntent
}
