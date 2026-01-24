package org.sake_hack.core.network

import io.ktor.client.*
import io.ktor.client.engine.darwin.*

/**
 * iOS固有のHTTPクライアント実装（Darwinエンジンを使用）
 */
actual fun httpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient = HttpClient(Darwin) {
    config(this)

    engine {
        configureRequest {
            setAllowsCellularAccess(true)
            setTimeoutInterval(30.0)
        }
    }
}
