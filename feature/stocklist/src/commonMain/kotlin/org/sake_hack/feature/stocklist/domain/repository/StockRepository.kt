package org.sake_hack.feature.stocklist.domain.repository

import org.sake_hack.feature.stocklist.domain.model.Stock
import org.sake_hack.feature.stocklist.domain.model.StockEditRequest
import org.sake_hack.feature.stocklist.domain.model.StockFilterCriteria
import org.sake_hack.feature.stocklist.domain.model.StockPageResult
import org.sake_hack.feature.stocklist.domain.model.StockSortCriteria

/**
 * 在庫リポジトリインターフェース
 */
interface StockRepository {
    /**
     * 在庫ページを取得
     *
     * @param offset オフセット
     * @param limit 取得件数
     * @param filterCriteria フィルター条件(nullの場合はフィルターなし)
     * @param sortCriteria ソート条件
     * @return 在庫ページング結果
     */
    suspend fun getStockPage(
        offset: Int,
        limit: Int,
        filterCriteria: StockFilterCriteria?,
        sortCriteria: StockSortCriteria
    ): Result<StockPageResult>

    /**
     * 在庫をIDで取得
     *
     * @param id 在庫ID
     * @return 在庫
     */
    suspend fun getStockById(id: Int): Result<Stock>

    /**
     * 在庫を作成
     *
     * @param request 在庫作成リクエスト
     * @return 作成された在庫
     */
    suspend fun createStock(request: StockEditRequest): Result<Stock>

    /**
     * 在庫を更新
     *
     * @param id 在庫ID
     * @param request 在庫更新リクエスト
     * @return 更新された在庫
     */
    suspend fun updateStock(id: Int, request: StockEditRequest): Result<Stock>

    /**
     * 在庫画像をアップロード
     *
     * @param id 在庫ID
     * @param imageData 画像データ
     * @return 画像URL
     */
    suspend fun uploadStockImage(id: Int, imageData: ByteArray): Result<String>
}
