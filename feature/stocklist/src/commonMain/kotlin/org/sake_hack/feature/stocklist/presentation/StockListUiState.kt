package org.sake_hack.feature.stocklist.presentation

import org.sake_hack.feature.stocklist.domain.model.Stock
import org.sake_hack.feature.stocklist.domain.model.StockEditField
import org.sake_hack.feature.stocklist.domain.model.StockEditRequest
import org.sake_hack.feature.stocklist.domain.model.StockFilterCriteria
import org.sake_hack.feature.stocklist.domain.model.StockSortCriteria

/**
 * 在庫一覧UI State
 */
data class StockListUiState(
    // ページネーション
    val displayedStocks: List<Stock> = emptyList(),
    val currentOffset: Int = 0,
    val limit: Int = 20,
    val totalCount: Long = 0,

    // ローディング状態
    val isInitialLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,

    // エラー状態
    val error: String? = null,

    // フィルター
    val filterCriteria: StockFilterCriteria = StockFilterCriteria(),
    val isFilterDialogOpen: Boolean = false,
    val tempFilterCriteria: StockFilterCriteria = StockFilterCriteria(),

    // ソート
    val sortCriteria: StockSortCriteria = StockSortCriteria(),
    val isSortMenuOpen: Boolean = false,

    // 詳細
    val selectedStock: Stock? = null,
    val isDetailDialogOpen: Boolean = false,

    // 編集
    val isEditMode: Boolean = false,
    val editingStock: StockEditRequest? = null,
    val isSaving: Boolean = false,
    val validationErrors: Map<StockEditField, String> = emptyMap(),

    // 追加
    val isCreateDialogOpen: Boolean = false,
    val creatingStock: StockEditRequest? = null,
    val isCreating: Boolean = false,
    val createValidationErrors: Map<StockEditField, String> = emptyMap(),

    // 画像
    val isImagePickerOpen: Boolean = false,
    val selectedImageSource: ImageSource? = null,
    val isCropMode: Boolean = false,
    val selectedImageData: ByteArray? = null,

    // 成功メッセージ
    val successMessage: String? = null
) {
    /**
     * 次のページが存在するかどうかを判定
     *
     * @return 次のページが存在する場合true
     */
    fun hasNextPage(): Boolean = currentOffset + displayedStocks.size < totalCount
}
