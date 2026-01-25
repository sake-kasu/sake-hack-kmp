package org.sake_hack.feature.stocklist.domain.model

/**
 * 在庫ソート条件
 *
 * @property field ソートフィールド
 * @property ascending 昇順の場合true、降順の場合false
 */
data class StockSortCriteria(
    val field: StockSortField = StockSortField.NAME,
    val ascending: Boolean = true
)

/**
 * 在庫ソートフィールド
 */
enum class StockSortField {
    /** 度数 */
    ABV,

    /** 酒名 */
    NAME,

    /** 残量 */
    REMAINING_VOLUME,

    /** 金額 */
    PRICE
}
