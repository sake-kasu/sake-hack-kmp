package org.sake_hack.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sake_hack.core.common.error.AppError
import org.sake_hack.core.common.error.NetworkErrorType
import org.sake_hack.feature.auth.domain.usecase.LoginAsGuestUseCase
import org.sake_hack.feature.auth.domain.usecase.LoginUseCase

/**
 * ログイン画面のViewModel
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val loginAsGuestUseCase: LoginAsGuestUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.LoginWithGoogle -> loginWithGoogle()
            is LoginIntent.LoginAsGuest -> loginAsGuest()
            is LoginIntent.ClearError -> clearError()
        }
    }

    private fun loginWithGoogle() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            loginUseCase()
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = AppError.Network(
                                errorMessage = error.message ?: "ログインに失敗しました",
                                errorCause = error,
                                errorType = NetworkErrorType.UNKNOWN
                            )
                        )
                    }
                }
        }
    }

    private fun loginAsGuest() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            loginAsGuestUseCase()
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = AppError.Network(
                                errorMessage = error.message ?: "ゲストログインに失敗しました",
                                errorCause = error,
                                errorType = NetworkErrorType.UNKNOWN
                            )
                        )
                    }
                }
        }
    }

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
