package org.sake_hack.feature.stocklist.domain.usecase

import org.sake_hack.feature.stocklist.domain.model.Stock
import org.sake_hack.feature.stocklist.domain.model.StockEditRequest
import org.sake_hack.feature.stocklist.domain.repository.StockRepository

/**
 * 在庫更新UseCase
 *
 * @property repository 在庫リポジトリ
 */
class UpdateStockUseCase(
    private val repository: StockRepository
) {
    /**
     * 在庫を更新
     *
     * @param id 在庫ID
     * @param request 在庫更新リクエスト
     * @return 更新された在庫
     */
    suspend operator fun invoke(id: Int, request: StockEditRequest): Result<Stock> =
        repository.updateStock(id, request)
}
