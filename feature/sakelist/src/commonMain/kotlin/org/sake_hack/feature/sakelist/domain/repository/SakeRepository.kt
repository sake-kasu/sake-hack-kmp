package org.sake_hack.feature.sakelist.domain.repository

import org.sake_hack.feature.sakelist.domain.model.FilterCriteria
import org.sake_hack.feature.sakelist.domain.model.Sake
import org.sake_hack.feature.sakelist.domain.model.SakePageResult
import org.sake_hack.feature.sakelist.domain.model.SakeSortCriteria

/**
 * 酒データ操作のためのRepositoryインターフェース
 * Clean Architectureに従い、ドメイン層が契約を定義
 */
interface SakeRepository {
    /**
     * ローカルキャッシュまたはリモートから全ての酒を取得
     */
    suspend fun getAllSake(): Result<List<Sake>>

    /**
     * IDで特定の酒を取得
     */
    suspend fun getSakeById(id: String): Result<Sake>

    /**
     * リモートから酒一覧を再取得してローカルキャッシュを更新
     */
    suspend fun refreshSakeList(): Result<Unit>

    /**
     * ページネーション対応の酒一覧取得
     * フィルター条件・ソート条件を含めてサーバー側で処理
     *
     * @param offset スキップする件数
     * @param limit 取得する件数
     * @param filterCriteria フィルター条件(nullの場合はフィルターなし)
     * @param sortCriteria ソート条件(nullの場合はデフォルトソート)
     * @return ページング結果(items、offset、limit、total)
     */
    suspend fun getSakePage(
        offset: Int,
        limit: Int,
        filterCriteria: FilterCriteria?,
        sortCriteria: SakeSortCriteria? = null
    ): Result<SakePageResult>

    /**
     * 酒にいいねを追加
     */
    suspend fun likeSake(sakeId: Int): Result<Unit>

    /**
     * 酒のいいねを削除
     */
    suspend fun unlikeSake(sakeId: Int): Result<Unit>
}
