package org.sake_hack.feature.sakelist.domain.usecase

import org.sake_hack.feature.sakelist.domain.model.FilterCriteria
import org.sake_hack.feature.sakelist.domain.model.SakePageResult
import org.sake_hack.feature.sakelist.domain.repository.SakeRepository

/**
 * ページネーション対応の酒一覧取得UseCase
 * ビジネスロジック層でページング処理を管理
 */
class GetSakePageUseCase(
    private val repository: SakeRepository
) {
    /**
     * 指定されたoffset、limit、フィルター条件で酒一覧を取得
     *
     * @param offset スキップする件数
     * @param limit 取得する件数(デフォルト: 20)
     * @param filterCriteria フィルター条件(nullの場合はフィルターなし)
     * @return ページング結果
     */
    suspend operator fun invoke(
        offset: Int,
        limit: Int = 20,
        filterCriteria: FilterCriteria?
    ): Result<SakePageResult> {
        return repository.getSakePage(
            offset = offset,
            limit = limit,
            filterCriteria = filterCriteria
        )
    }
}
