package org.sake_hack.core.network

/**
 * iOS環境のAPI設定
 */
actual object ApiConfig {
    // iOSシミュレータからローカルホストにアクセス
    actual val baseUrl: String = "http://localhost:8080"
}
