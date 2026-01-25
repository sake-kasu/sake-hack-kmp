package org.sake_hack.feature.stocklist.presentation

import org.sake_hack.feature.stocklist.domain.model.Stock
import org.sake_hack.feature.stocklist.domain.model.StockEditField
import org.sake_hack.feature.stocklist.domain.model.StockSortCriteria

/**
 * 在庫一覧Intent
 */
sealed interface StockListIntent {
    // データロード
    data object LoadStockList : StockListIntent
    data object LoadNextPage : StockListIntent
    data object Refresh : StockListIntent

    // フィルター
    data object OpenFilterDialog : StockListIntent
    data object CloseFilterDialog : StockListIntent
    data class UpdateNameFilter(val name: String?) : StockListIntent
    data class ToggleCategoryFilter(val category: String) : StockListIntent
    data class UpdateRegionFilter(val region: String?) : StockListIntent
    data object ApplyFilter : StockListIntent
    data object ClearFilter : StockListIntent

    // ソート
    data object OpenSortMenu : StockListIntent
    data object CloseSortMenu : StockListIntent
    data class ApplySort(val sortCriteria: StockSortCriteria) : StockListIntent

    // 詳細
    data class OpenStockDetail(val stock: Stock) : StockListIntent
    data object CloseDetailDialog : StockListIntent

    // 編集
    data object StartEdit : StockListIntent
    data object CancelEdit : StockListIntent
    data class UpdateEditField(val field: StockEditField, val value: Any) : StockListIntent
    data object SaveEdit : StockListIntent

    // 追加
    data object OpenCreateDialog : StockListIntent
    data object CancelCreate : StockListIntent
    data class UpdateCreateField(val field: StockEditField, val value: Any) : StockListIntent
    data object SaveCreate : StockListIntent

    // 画像
    data object OpenImagePicker : StockListIntent
    data class SelectImageSource(val source: ImageSource) : StockListIntent
    data class CropImage(val croppedData: ByteArray) : StockListIntent
}

/**
 * 画像ソース
 */
enum class ImageSource {
    /** カメラ */
    CAMERA,

    /** ギャラリー */
    GALLERY
}
