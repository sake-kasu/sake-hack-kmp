package org.sake_hack.feature.sakelist.domain.model

/**
 * 酒ソート条件
 *
 * @property field ソートフィールド
 * @property ascending 昇順の場合true、降順の場合false
 */
data class SakeSortCriteria(
    val field: SakeSortField = SakeSortField.NAME,
    val ascending: Boolean = true
)

/**
 * 酒ソートフィールド
 */
enum class SakeSortField {
    /** 酒名 */
    NAME,

    /** 種類 */
    TYPE,

    /** 産地 */
    REGION
}
