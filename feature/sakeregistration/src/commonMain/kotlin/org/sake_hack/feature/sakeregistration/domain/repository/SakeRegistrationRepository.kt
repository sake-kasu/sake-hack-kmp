package org.sake_hack.feature.sakeregistration.domain.repository

import org.sake_hack.feature.sakeregistration.domain.model.CreateSakeRequest
import org.sake_hack.feature.sakeregistration.domain.model.SakeRegistrationResult

/**
 * 酒登録リポジトリのインターフェース
 * Clean Architectureに従い、ドメイン層が契約を定義
 */
interface SakeRegistrationRepository {
    /**
     * 酒を登録する
     *
     * @param request 酒登録リクエスト
     * @return 登録結果(成功時は登録された酒のID)
     */
    suspend fun createSake(request: CreateSakeRequest): Result<SakeRegistrationResult>
}
