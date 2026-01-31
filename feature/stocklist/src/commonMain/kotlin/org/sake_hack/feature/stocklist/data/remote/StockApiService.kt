package org.sake_hack.feature.stocklist.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.sake_hack.core.network.ApiConfig
import org.sake_hack.feature.stocklist.data.remote.dto.ImageUploadResponseDto
import org.sake_hack.feature.stocklist.data.remote.dto.StockDto
import org.sake_hack.feature.stocklist.data.remote.dto.StockEditRequestDto
import org.sake_hack.feature.stocklist.data.remote.dto.StockPageResponseDto

/**
 * 在庫APIサービス
 * Ktor HttpClientを使用して実際のAPIを呼び出す
 */
class StockApiService(
    private val httpClient: HttpClient
) {
    private val baseUrl = ApiConfig.baseUrl

    /**
     * 在庫ページを取得
     *
     * @param offset オフセット
     * @param limit 取得件数
     * @param name 酒名でフィルタ(部分一致)
     * @param categories 種類でフィルタ(カンマ区切り)
     * @param region 産地でフィルタ(部分一致)
     * @param sortBy ソートフィールド(abv, name, remainingVolume, price)
     * @param order ソート順(asc, desc)
     * @return 在庫ページレスポンス
     */
    suspend fun getStockPage(
        offset: Int = 0,
        limit: Int = 20,
        name: String? = null,
        categories: String? = null,
        region: String? = null,
        sortBy: String = "name",
        order: String = "asc"
    ): StockPageResponseDto {
        return httpClient.get("$baseUrl/stocks") {
            parameter("offset", offset)
            parameter("limit", limit)
            name?.let { parameter("name", it) }
            categories?.let { parameter("categories", it) }
            region?.let { parameter("region", it) }
            parameter("sortBy", sortBy)
            parameter("order", order)
        }.body()
    }

    /**
     * 在庫をIDで取得
     *
     * @param id 在庫ID
     * @return 在庫DTO
     */
    suspend fun getStockById(id: Int): StockDto {
        return httpClient.get("$baseUrl/stocks/$id").body()
    }

    /**
     * 在庫を作成
     *
     * @param request 在庫作成リクエスト
     * @return 作成された在庫DTO
     */
    suspend fun createStock(request: StockEditRequestDto): StockDto {
        return httpClient.post("$baseUrl/stocks") {
            setBody(request)
        }.body()
    }

    /**
     * 在庫を更新
     *
     * @param id 在庫ID
     * @param request 在庫更新リクエスト
     * @return 更新された在庫DTO
     */
    suspend fun updateStock(id: Int, request: StockEditRequestDto): StockDto {
        return httpClient.put("$baseUrl/stocks/$id") {
            setBody(request)
        }.body()
    }

    /**
     * 在庫画像をアップロード
     *
     * @param id 在庫ID
     * @param imageData 画像データ
     * @return アップロードされた画像URL
     */
    suspend fun uploadStockImage(id: Int, imageData: ByteArray): ImageUploadResponseDto {
        return httpClient.post("$baseUrl/stocks/$id/image") {
            setBody(imageData)
        }.body()
    }
}
