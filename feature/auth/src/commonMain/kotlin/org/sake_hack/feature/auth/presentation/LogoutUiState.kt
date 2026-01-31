package org.sake_hack.feature.auth.presentation

import org.sake_hack.core.common.error.AppError

/**
 * ログアウト機能のUI状態
 */
data class LogoutUiState(
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val isLogoutSuccess: Boolean = false
)
