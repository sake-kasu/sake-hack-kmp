package org.sake_hack.feature.stocklist.data.repository

import org.sake_hack.feature.stocklist.data.mapper.toDomain
import org.sake_hack.feature.stocklist.data.mapper.toDto
import org.sake_hack.feature.stocklist.data.remote.StockApiService
import org.sake_hack.feature.stocklist.domain.model.Stock
import org.sake_hack.feature.stocklist.domain.model.StockEditRequest
import org.sake_hack.feature.stocklist.domain.model.StockFilterCriteria
import org.sake_hack.feature.stocklist.domain.model.StockPageResult
import org.sake_hack.feature.stocklist.domain.model.StockSortCriteria
import org.sake_hack.feature.stocklist.domain.repository.StockRepository

/**
 * 在庫リポジトリ実装
 *
 * @property apiService モックAPIサービス
 */
class StockRepositoryImpl(
    private val apiService: StockApiService
) : StockRepository {

    override suspend fun getStockPage(
        offset: Int,
        limit: Int,
        filterCriteria: StockFilterCriteria?,
        sortCriteria: StockSortCriteria
    ): Result<StockPageResult> = runCatching {
        // フィルター条件をAPIパラメータに変換
        val name = filterCriteria?.name?.takeIf { it.isNotBlank() }
        val categories = filterCriteria?.categories?.takeIf { it.isNotEmpty() }?.joinToString(",")
        val region = filterCriteria?.region?.takeIf { it.isNotBlank() }

        // ソート条件をAPIパラメータに変換
        val sortBy = when (sortCriteria.field) {
            org.sake_hack.feature.stocklist.domain.model.StockSortField.ABV -> "abv"
            org.sake_hack.feature.stocklist.domain.model.StockSortField.NAME -> "name"
            org.sake_hack.feature.stocklist.domain.model.StockSortField.REMAINING_VOLUME -> "remainingVolume"
            org.sake_hack.feature.stocklist.domain.model.StockSortField.PRICE -> "price"
        }
        val order = if (sortCriteria.ascending) "asc" else "desc"

        // API呼び出し
        val response = apiService.getStockPage(
            offset = offset,
            limit = limit,
            name = name,
            categories = categories,
            region = region,
            sortBy = sortBy,
            order = order
        )

        // Domain モデルに変換
        response.toDomain()
    }

    override suspend fun getStockById(id: Int): Result<Stock> = runCatching {
        val dto = apiService.getStockById(id)
        dto.toDomain()
    }

    override suspend fun createStock(request: StockEditRequest): Result<Stock> = runCatching {
        val dto = apiService.createStock(request.toDto())
        dto.toDomain()
    }

    override suspend fun updateStock(id: Int, request: StockEditRequest): Result<Stock> = runCatching {
        val dto = apiService.updateStock(id, request.toDto())
        dto.toDomain()
    }

    override suspend fun uploadStockImage(id: Int, imageData: ByteArray): Result<String> = runCatching {
        val response = apiService.uploadStockImage(id, imageData)
        response.imageUrl
    }
}
