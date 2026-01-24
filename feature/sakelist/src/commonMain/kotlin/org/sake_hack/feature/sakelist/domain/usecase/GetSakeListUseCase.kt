package org.sake_hack.feature.sakelist.domain.usecase

import org.sake_hack.feature.sakelist.domain.model.Sake
import org.sake_hack.feature.sakelist.domain.repository.SakeRepository

/**
 * Repositoryから酒一覧を取得するユースケース
 */
class GetSakeListUseCase(
    private val repository: SakeRepository
) {
    suspend operator fun invoke(): Result<List<Sake>> {
        return repository.getAllSake()
    }
}
