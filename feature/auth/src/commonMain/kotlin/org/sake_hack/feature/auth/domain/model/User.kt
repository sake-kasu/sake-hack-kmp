package org.sake_hack.feature.auth.domain.model

import org.sake_hack.domain.model.UserRole

/**
 * 認証ユーザーのドメインモデル
 */
data class User(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val role: UserRole
)
