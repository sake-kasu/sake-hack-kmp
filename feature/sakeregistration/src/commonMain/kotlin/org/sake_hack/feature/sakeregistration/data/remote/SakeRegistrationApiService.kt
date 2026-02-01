package org.sake_hack.feature.sakeregistration.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.sake_hack.core.network.ApiConfig
import org.sake_hack.feature.sakeregistration.data.remote.dto.CreateSakeRequestDto
import org.sake_hack.feature.sakeregistration.data.remote.dto.SakeRegistrationResponseDto

/**
 * 酒登録APIサービス
 * Ktor HttpClientを使用して実際のAPIを呼び出す
 */
class SakeRegistrationApiService(
    private val httpClient: HttpClient
) {
    private val baseUrl = ApiConfig.baseUrl

    /**
     * 酒を登録する
     *
     * @param request 酒登録リクエスト
     * @return 登録結果(成功時は登録された酒のID)
     */
    suspend fun createSake(request: CreateSakeRequestDto): SakeRegistrationResponseDto {
        return httpClient.post("$baseUrl/sakes") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
