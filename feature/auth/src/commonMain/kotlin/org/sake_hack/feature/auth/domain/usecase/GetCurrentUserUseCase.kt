package org.sake_hack.feature.auth.domain.usecase

import org.sake_hack.feature.auth.domain.model.User
import org.sake_hack.feature.auth.domain.repository.AuthRepository

/**
 * 現在ログインしているユーザーを取得するユースケース
 */
class GetCurrentUserUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<User?> = repository.getCurrentUser()
}
