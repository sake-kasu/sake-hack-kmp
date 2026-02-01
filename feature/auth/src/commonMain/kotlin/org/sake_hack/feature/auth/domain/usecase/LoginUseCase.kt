package org.sake_hack.feature.auth.domain.usecase

import org.sake_hack.feature.auth.domain.model.User
import org.sake_hack.feature.auth.domain.repository.AuthRepository

/**
 * Googleアカウントでログインするユースケース
 */
class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<User> = repository.loginWithGoogle()
}
