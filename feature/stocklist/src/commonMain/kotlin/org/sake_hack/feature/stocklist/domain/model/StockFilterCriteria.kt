package org.sake_hack.feature.stocklist.domain.model

/**
 * 在庫フィルター条件
 *
 * @property name 酒名でフィルタ(部分一致)
 * @property categories 種類でフィルタ(複数選択可能)
 * @property region 産地でフィルタ(部分一致)
 */
data class StockFilterCriteria(
    val name: String? = null,
    val categories: Set<String> = emptySet(),
    val region: String? = null
) {
    /**
     * フィルターが有効かどうかを判定
     *
     * @return フィルター条件が1つでも設定されている場合true
     */
    fun isActive(): Boolean = name != null || categories.isNotEmpty() || region != null

    /**
     * 有効なフィルター条件の数を取得
     *
     * @return 設定されているフィルター条件の数
     */
    fun activeFilterCount(): Int = listOfNotNull(name, region).size + categories.size
}
