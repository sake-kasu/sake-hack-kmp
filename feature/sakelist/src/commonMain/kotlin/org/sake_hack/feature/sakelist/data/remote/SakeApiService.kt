package org.sake_hack.feature.sakelist.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.sake_hack.core.network.ApiConfig
import org.sake_hack.feature.sakelist.data.remote.dto.SakeDto
import org.sake_hack.feature.sakelist.data.remote.dto.SakePageResponseDto

/**
 * 酒データを取得するAPIサービス
 * Ktor HttpClientを使用して実際のAPIを呼び出す
 */
class SakeApiService(
    private val httpClient: HttpClient
) {
    private val baseUrl = ApiConfig.baseUrl

    /**
     * ページネーション対応の酒一覧取得
     *
     * @param offset スキップする件数
     * @param limit 取得する件数
     * @param sakeName 酒名でフィルタ（部分一致）
     * @param sakeTypes 酒の種類でフィルタ（複数選択可能）
     * @param region 産地でフィルタ（部分一致）
     */
    suspend fun fetchSakePage(
        offset: Int,
        limit: Int,
        sakeName: String? = null,
        sakeTypes: Set<String>? = null,
        region: String? = null
    ): SakePageResponseDto {
        return httpClient.get("$baseUrl/sakes") {
            parameter("offset", offset)
            parameter("limit", limit)
            sakeName?.takeIf { it.isNotBlank() }?.let { parameter("name", it) }
            sakeTypes?.takeIf { it.isNotEmpty() }?.let {
                parameter("sakeTypes", it.joinToString(","))
            }
            region?.takeIf { it.isNotBlank() }?.let { parameter("region", it) }
        }.body()
    }

    /**
     * IDで特定の酒を取得
     */
    suspend fun fetchSakeById(id: Int): SakeDto {
        return httpClient.get("$baseUrl/sakes/$id").body()
    }
}
