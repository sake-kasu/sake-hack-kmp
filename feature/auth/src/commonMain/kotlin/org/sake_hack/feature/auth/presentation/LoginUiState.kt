package org.sake_hack.feature.auth.presentation

import org.sake_hack.core.common.error.AppError

/**
 * ログイン画面のUI状態
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val isLoginSuccess: Boolean = false
)
