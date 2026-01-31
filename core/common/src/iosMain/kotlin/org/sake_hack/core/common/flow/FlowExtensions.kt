package org.sake_hack.core.common.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/**
 * iOS 専用の Flow 拡張
 *
 * StateFlow を Swift の @Published と連携させるためのユーティリティ
 */

/**
 * StateFlow の現在値を取得
 * Swift から呼び出し可能
 */
fun <T> StateFlow<T>.currentValue(): T = value

/**
 * Flow を監視してメインスレッドでコールバック
 * Swift の UI 更新に適した形式
 */
fun <T> Flow<T>.collectOnMain(
    scope: CoroutineScope,
    callback: (T) -> Unit
): Job {
    return onEach { value ->
        dispatch_async(dispatch_get_main_queue()) {
            callback(value)
        }
    }.launchIn(scope)
}

/**
 * iOS 向けの簡易監視ヘルパー
 * Closeable を返すので Swift の deinit で close() を呼ぶだけでOK
 */
fun <T> StateFlow<T>.observe(callback: (T) -> Unit): Closeable {
    val job = Job()
    val scope = CoroutineScope(Dispatchers.Main + job)

    // 初期値を即座に通知
    callback(value)

    // 以降の変更を監視
    onEach { newValue ->
        callback(newValue)
    }.launchIn(scope)

    return object : Closeable {
        override fun close() {
            job.cancel()
        }
    }
}
