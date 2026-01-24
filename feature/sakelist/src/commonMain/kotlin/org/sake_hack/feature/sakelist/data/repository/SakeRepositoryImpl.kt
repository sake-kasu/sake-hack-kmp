package org.sake_hack.feature.sakelist.data.repository

import org.sake_hack.core.network.error.safeApiCall
import org.sake_hack.feature.sakelist.data.local.SakeLocalDataSource
import org.sake_hack.feature.sakelist.data.remote.SakeApiService
import org.sake_hack.feature.sakelist.domain.model.FilterCriteria
import org.sake_hack.feature.sakelist.domain.model.Sake
import org.sake_hack.feature.sakelist.domain.model.SakePageResult
import org.sake_hack.feature.sakelist.domain.repository.SakeRepository

/**
 * SakeRepositoryの実装
 * キャッシュファースト戦略を実装: ローカルデータがあれば使用、リフレッシュ時はネットワークから取得
 */
class SakeRepositoryImpl(
    private val apiService: SakeApiService,
    private val localDataSource: SakeLocalDataSource
) : SakeRepository {

    /**
     * 全ての酒を取得 - キャッシュファースト戦略
     * キャッシュされたデータがあればそれを返し、なければネットワークから取得
     */
    override suspend fun getAllSake(): Result<List<Sake>> = safeApiCall {
        val cached = localDataSource.getAllSake()
        if (cached.isNotEmpty()) {
            return@safeApiCall cached
        }

        // キャッシュが空の場合、ページネーションAPIで全件取得
        val response = apiService.fetchSakePage(offset = 0, limit = 100)
        val sakes = response.data.map { it.toDomain() }
        localDataSource.saveSakeList(sakes)
        sakes
    }

    /**
     * IDで特定の酒を取得
     */
    override suspend fun getSakeById(id: String): Result<Sake> = safeApiCall {
        // まずローカルキャッシュから取得を試みる
        val cached = localDataSource.getSakeById(id)
        if (cached != null) {
            return@safeApiCall cached
        }

        // キャッシュにない場合、ネットワークから取得
        val dto = apiService.fetchSakeById(id.toInt())
        dto.toDomain()
    }

    /**
     * ネットワークから酒一覧を再取得してローカルキャッシュを更新
     */
    override suspend fun refreshSakeList(): Result<Unit> = safeApiCall {
        val response = apiService.fetchSakePage(offset = 0, limit = 100)
        val sakes = response.data.map { it.toDomain() }
        localDataSource.deleteAll()
        localDataSource.saveSakeList(sakes)
    }

    /**
     * ページネーション対応の酒一覧取得
     * フィルター条件をAPIに渡してサーバー側で処理
     */
    override suspend fun getSakePage(
        offset: Int,
        limit: Int,
        filterCriteria: FilterCriteria?
    ): Result<SakePageResult> = safeApiCall {
        val response = apiService.fetchSakePage(
            offset = offset,
            limit = limit,
            sakeName = filterCriteria?.sakeName,
            sakeTypes = filterCriteria?.sakeTypes,
            region = filterCriteria?.region
        )

        SakePageResult(
            items = response.data.map { it.toDomain() },
            total = response.meta.total,
            offset = response.meta.offset,
            limit = response.meta.limit
        )
    }
}
