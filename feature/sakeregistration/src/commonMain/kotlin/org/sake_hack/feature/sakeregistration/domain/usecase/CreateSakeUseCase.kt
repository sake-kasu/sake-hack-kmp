package org.sake_hack.feature.sakeregistration.domain.usecase

import org.sake_hack.feature.sakeregistration.domain.model.CreateSakeRequest
import org.sake_hack.feature.sakeregistration.domain.model.SakeRegistrationResult
import org.sake_hack.feature.sakeregistration.domain.repository.SakeRegistrationRepository

/**
 * 酒を登録するUseCase
 */
class CreateSakeUseCase(
    private val repository: SakeRegistrationRepository
) {
    /**
     * 酒を登録する
     *
     * @param request 酒登録リクエスト
     * @return 登録結果
     */
    suspend operator fun invoke(request: CreateSakeRequest): Result<SakeRegistrationResult> {
        return repository.createSake(request)
    }
}
