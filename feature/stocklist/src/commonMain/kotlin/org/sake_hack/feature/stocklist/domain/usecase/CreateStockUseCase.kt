package org.sake_hack.feature.stocklist.domain.usecase

import org.sake_hack.feature.stocklist.domain.model.Stock
import org.sake_hack.feature.stocklist.domain.model.StockEditRequest
import org.sake_hack.feature.stocklist.domain.repository.StockRepository

/**
 * 在庫作成UseCase
 *
 * @property repository 在庫リポジトリ
 */
class CreateStockUseCase(
    private val repository: StockRepository
) {
    /**
     * 在庫を作成
     *
     * @param request 在庫作成リクエスト
     * @return 作成された在庫
     */
    suspend operator fun invoke(request: StockEditRequest): Result<Stock> =
        repository.createStock(request)
}
