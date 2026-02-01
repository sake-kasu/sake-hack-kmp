package org.sake_hack.feature.auth.domain.usecase

import org.sake_hack.feature.auth.domain.repository.AuthRepository

/**
 * ログアウトするユースケース
 */
class LogoutUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> = repository.logout()
}
