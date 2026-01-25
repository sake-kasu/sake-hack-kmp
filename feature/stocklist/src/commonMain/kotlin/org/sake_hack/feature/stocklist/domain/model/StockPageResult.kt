package org.sake_hack.feature.stocklist.domain.model

/**
 * 在庫ページング結果
 *
 * @property items 在庫リスト
 * @property total 全件数
 * @property offset オフセット
 * @property limit 取得件数
 */
data class StockPageResult(
    val items: List<Stock>,
    val total: Long,
    val offset: Int,
    val limit: Int
) {
    /**
     * 次のページが存在するかどうかを判定
     *
     * @return 次のページが存在する場合true
     */
    fun hasNextPage(): Boolean = offset + items.size < total
}
