package org.sake_hack.feature.sakelist.domain.model

/**
 * ページング結果を表すドメインモデル
 * APIのページネーションレスポンスをドメイン層で扱うための型
 */
data class SakePageResult(
    val items: List<Sake>,
    val total: Long,
    val offset: Int,
    val limit: Int
) {
    /**
     * 次のページが存在するかどうか
     */
    fun hasNextPage(): Boolean = offset + limit < total
}
