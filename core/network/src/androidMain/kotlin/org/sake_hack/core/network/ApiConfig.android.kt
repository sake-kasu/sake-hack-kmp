package org.sake_hack.core.network

/**
 * Android環境のAPI設定
 */
actual object ApiConfig {
    // Android EmulatorからローカルホストにアクセスするURLは10.0.2.2
    // 実機の場合は開発マシンのローカルIPアドレスを使用
    actual val baseUrl: String = "http://10.0.2.2:8080/api"
}
