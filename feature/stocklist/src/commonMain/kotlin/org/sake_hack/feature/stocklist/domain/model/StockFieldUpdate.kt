package org.sake_hack.feature.stocklist.domain.model

/**
 * 在庫フィールド更新の型安全な表現
 *
 * 各フィールドの型を明示することで、コンパイル時に型チェックを実現
 */
sealed interface StockFieldUpdate {
    /**
     * 名前更新（必須、String）
     */
    data class UpdateName(val value: String) : StockFieldUpdate

    /**
     * ふりがな更新（必須、String）
     */
    data class UpdateKana(val value: String) : StockFieldUpdate

    /**
     * 大分類更新（必須、String）
     */
    data class UpdateMainCategory(val value: String) : StockFieldUpdate

    /**
     * 小分類更新（オプショナル、String?）
     */
    data class UpdateSubCategory(val value: String?) : StockFieldUpdate

    /**
     * 産地更新（オプショナル、String?）
     */
    data class UpdateRegion(val value: String?) : StockFieldUpdate

    /**
     * 度数更新（必須、Int）
     */
    data class UpdateAbv(val value: Int) : StockFieldUpdate

    /**
     * 購入時容量更新（必須、Int）
     */
    data class UpdateInitialVolume(val value: Int) : StockFieldUpdate

    /**
     * 残容量更新（必須、Int）
     */
    data class UpdateRemainingVolume(val value: Int) : StockFieldUpdate

    /**
     * 購入時価格更新（必須、Int）
     */
    data class UpdatePurchasePrice(val value: Int) : StockFieldUpdate

    /**
     * 自由記述更新（オプショナル、String?）
     */
    data class UpdateNotes(val value: String?) : StockFieldUpdate

    /**
     * 画像URL更新（オプショナル、String?）
     */
    data class UpdateImageUrl(val value: String?) : StockFieldUpdate
}
