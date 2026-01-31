package org.sake_hack.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sake_hack.feature.auth.domain.usecase.IsSessionValidUseCase

/**
 * セッション状態を管理するViewModel
 */
class SessionViewModel(
    private val isSessionValidUseCase: IsSessionValidUseCase
) : ViewModel() {

    private val _sessionState = MutableStateFlow(SessionState())
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    fun checkSession() {
        viewModelScope.launch {
            _sessionState.update { it.copy(isLoading = true) }
            isSessionValidUseCase()
                .onSuccess { isValid ->
                    _sessionState.update { it.copy(isLoading = false, isSessionValid = isValid) }
                }
                .onFailure {
                    _sessionState.update { it.copy(isLoading = false, isSessionValid = false) }
                }
        }
    }
}

/**
 * セッション状態
 */
data class SessionState(
    val isLoading: Boolean = false,
    val isSessionValid: Boolean = false
)
