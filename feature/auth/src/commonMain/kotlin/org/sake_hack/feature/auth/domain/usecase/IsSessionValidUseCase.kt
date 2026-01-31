package org.sake_hack.feature.auth.domain.usecase

import org.sake_hack.feature.auth.domain.repository.AuthRepository

/**
 * セッションが有効かどうかを確認するユースケース
 */
class IsSessionValidUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<Boolean> = repository.isSessionValid()
}
