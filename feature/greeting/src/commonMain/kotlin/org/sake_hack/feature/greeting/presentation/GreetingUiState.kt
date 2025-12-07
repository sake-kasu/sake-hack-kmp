package org.sake_hack.feature.greeting.presentation

/**
 * UI State for Greeting feature following MVI pattern.
 * Immutable data class representing the complete UI state.
 */
data class GreetingUiState(
    val greetingMessage: String = ""
)
