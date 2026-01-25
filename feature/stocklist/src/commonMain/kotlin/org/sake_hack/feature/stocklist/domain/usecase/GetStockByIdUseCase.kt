package org.sake_hack.feature.stocklist.domain.usecase

import org.sake_hack.feature.stocklist.domain.model.Stock
import org.sake_hack.feature.stocklist.domain.repository.StockRepository

/**
 * 在庫詳細取得UseCase
 *
 * @property repository 在庫リポジトリ
 */
class GetStockByIdUseCase(
    private val repository: StockRepository
) {
    /**
     * 在庫をIDで取得
     *
     * @param id 在庫ID
     * @return 在庫
     */
    suspend operator fun invoke(id: Int): Result<Stock> = repository.getStockById(id)
}
