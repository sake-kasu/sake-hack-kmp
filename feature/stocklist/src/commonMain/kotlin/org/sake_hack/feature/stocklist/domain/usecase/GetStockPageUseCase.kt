package org.sake_hack.feature.stocklist.domain.usecase

import org.sake_hack.feature.stocklist.domain.model.StockFilterCriteria
import org.sake_hack.feature.stocklist.domain.model.StockPageResult
import org.sake_hack.feature.stocklist.domain.model.StockSortCriteria
import org.sake_hack.feature.stocklist.domain.repository.StockRepository

/**
 * 在庫ページ取得UseCase
 *
 * @property repository 在庫リポジトリ
 */
class GetStockPageUseCase(
    private val repository: StockRepository
) {
    /**
     * 在庫ページを取得
     *
     * @param offset オフセット
     * @param limit 取得件数
     * @param filterCriteria フィルター条件(nullの場合はフィルターなし)
     * @param sortCriteria ソート条件
     * @return 在庫ページング結果
     */
    suspend operator fun invoke(
        offset: Int = 0,
        limit: Int = 20,
        filterCriteria: StockFilterCriteria? = null,
        sortCriteria: StockSortCriteria = StockSortCriteria()
    ): Result<StockPageResult> = repository.getStockPage(
        offset = offset,
        limit = limit,
        filterCriteria = filterCriteria,
        sortCriteria = sortCriteria
    )
}
