package org.sake_hack.feature.sakeregistration.data.repository

import org.sake_hack.core.network.error.safeApiCall
import org.sake_hack.feature.sakeregistration.data.remote.SakeRegistrationApiService
import org.sake_hack.feature.sakeregistration.data.remote.dto.CreateSakeRequestDto
import org.sake_hack.feature.sakeregistration.domain.model.CreateSakeRequest
import org.sake_hack.feature.sakeregistration.domain.model.SakeRegistrationResult
import org.sake_hack.feature.sakeregistration.domain.repository.SakeRegistrationRepository

/**
 * SakeRegistrationRepositoryの実装
 */
class SakeRegistrationRepositoryImpl(
    private val apiService: SakeRegistrationApiService
) : SakeRegistrationRepository {

    /**
     * 酒を登録する
     */
    override suspend fun createSake(request: CreateSakeRequest): Result<SakeRegistrationResult> = safeApiCall {
        val requestDto = CreateSakeRequestDto.fromDomain(request)
        val responseDto = apiService.createSake(requestDto)
        responseDto.toDomain()
    }
}
