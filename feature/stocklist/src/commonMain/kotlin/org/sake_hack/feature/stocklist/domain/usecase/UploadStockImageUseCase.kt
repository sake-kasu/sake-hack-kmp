package org.sake_hack.feature.stocklist.domain.usecase

import org.sake_hack.feature.stocklist.domain.repository.StockRepository

/**
 * 在庫画像アップロードUseCase
 *
 * @property repository 在庫リポジトリ
 */
class UploadStockImageUseCase(
    private val repository: StockRepository
) {
    /**
     * 在庫画像をアップロード
     *
     * @param id 在庫ID
     * @param imageData 画像データ
     * @return 画像URL
     */
    suspend operator fun invoke(id: Int, imageData: ByteArray): Result<String> =
        repository.uploadStockImage(id, imageData)
}
