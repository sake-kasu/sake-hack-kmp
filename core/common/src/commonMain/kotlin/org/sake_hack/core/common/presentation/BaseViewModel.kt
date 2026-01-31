package org.sake_hack.core.common.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sake_hack.core.common.error.AppError

/**
 * ViewModel パターンの基底 
 *
 * 特徴:
 * - 単一の不変 UiState
 * - Intent ベースのアクション処理
 * - Side Effect (一回限りのイベント) 管理
 *
 * @param State UI状態を表すデータクラス
 * @param Intent ユーザーアクションを表すsealed interface
 * @param Effect 一回限りのイベント(ナビゲーション、Snackbar等)
 */
abstract class BaseViewModel<State, Intent, Effect>(
    initialState: State
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    /**
     * 現在の状態を取得
     */
    protected val currentState: State
        get() = _uiState.value

    /**
     * Intent を処理
     * 子クラスで実装
     */
    abstract fun handleIntent(intent: Intent)

    /**
     * 状態を更新
     */
    protected fun updateState(reducer: (State) -> State) {
        _uiState.update(reducer)
    }

    /**
     * Side Effect を送信
     */
    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    /**
     * 安全な非同期処理
     * エラーを AppError に変換して処理
     */
    protected fun launchSafe(
        onError: (AppError) -> Unit = {},
        block: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                val appError = when (e) {
                    is AppError -> e
                    else -> AppError.Unknown(
                        errorMessage = e.message ?: "Unknown error",
                        errorCause = e
                    )
                }
                onError(appError)
            }
        }
    }

    /**
     * Result を処理するヘルパー
     */
    protected inline fun <T> Result<T>.handle(
        onSuccess: (T) -> Unit,
        onFailure: (AppError) -> Unit
    ) {
        this.onSuccess { onSuccess(it) }
            .onFailure { exception ->
                val appError = when (exception) {
                    is AppError -> exception
                    else -> AppError.Unknown(
                        errorMessage = exception.message ?: "Unknown error",
                        errorCause = exception
                    )
                }
                onFailure(appError)
            }
    }
}
