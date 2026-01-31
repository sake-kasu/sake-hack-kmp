package org.sake_hack.core.common.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * iOS (Swift) から Kotlin の Flow を監視するためのラッパー
 *
 * SwiftUI の @Published や Combine と連携するために使用
 *
 * 使用例 (Swift):
 * ```swift
 * class SakeListModel: ObservableObject {
 *     @Published var state: SakeListUiState
 *     private var stateJob: Closeable?
 *
 *     init(viewModel: SakeListViewModel) {
 *         self.state = viewModel.uiState.value
 *         stateJob = viewModel.uiState.watch { [weak self] state in
 *             self?.state = state
 *         }
 *     }
 *
 *     deinit {
 *         stateJob?.close()
 *     }
 * }
 * ```
 */
class CFlow<T>(private val flow: Flow<T>) : Flow<T> by flow {

    /**
     * Flow を監視して変更を通知
     *
     * @param block 新しい値が発行されるたびに呼び出されるコールバック
     * @return 監視を停止するための Closeable
     */
    fun watch(block: (T) -> Unit): Closeable {
        val job = Job()
        val scope = CoroutineScope(Dispatchers.Main + job)

        flow.onEach { value ->
            block(value)
        }.launchIn(scope)

        return object : Closeable {
            override fun close() {
                job.cancel()
            }
        }
    }
}

/**
 * 監視を停止するためのインターフェース
 */
interface Closeable {
    fun close()
}

/**
 * StateFlow を CFlow に変換
 */
fun <T> StateFlow<T>.wrap(): CFlow<T> = CFlow(this)

/**
 * Flow を CFlow に変換
 */
fun <T> Flow<T>.wrap(): CFlow<T> = CFlow(this)
