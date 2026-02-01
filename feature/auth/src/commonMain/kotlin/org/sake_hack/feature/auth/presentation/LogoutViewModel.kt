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
import org.sake_hack.feature.auth.domain.usecase.LogoutUseCase

/**
 * ログアウト機能のViewModel
 */
class LogoutViewModel(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogoutUiState())
    val uiState: StateFlow<LogoutUiState> = _uiState.asStateFlow()

    fun handleIntent(intent: LogoutIntent) {
        when (intent) {
            is LogoutIntent.Logout -> logout()
            is LogoutIntent.ClearError -> clearError()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            logoutUseCase()
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isLogoutSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = AppError.Network(
                                errorMessage = error.message ?: "ログアウトに失敗しました",
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
