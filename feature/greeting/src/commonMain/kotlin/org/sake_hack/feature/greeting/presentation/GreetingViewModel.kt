package org.sake_hack.feature.greeting.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.sake_hack.feature.greeting.domain.usecase.GetPlatformInfoUseCase
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

/**
 * ViewModel for Greeting feature following MVI pattern.
 * Manages UI state and business logic invocation.
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("GreetingViewModel")
class GreetingViewModel(
    private val getPlatformInfoUseCase: GetPlatformInfoUseCase = GetPlatformInfoUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GreetingUiState())
    val uiState: StateFlow<GreetingUiState> = _uiState.asStateFlow()

    init {
        loadGreeting()
    }

    private fun loadGreeting() {
        val platformName = getPlatformInfoUseCase()
        _uiState.update {
            it.copy(greetingMessage = "Hello, $platformName!")
        }
    }

    // For backwards compatibility with existing code
    fun greet(): String = _uiState.value.greetingMessage
}
