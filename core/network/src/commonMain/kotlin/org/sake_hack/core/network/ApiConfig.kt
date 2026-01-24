package org.sake_hack.core.network

/**
 * API設定クラス
 * 環境別にbaseURLを切り替える
 */
expect object ApiConfig {
    /**
     * API BaseURL
     * プラットフォームごとに設定可能
     */
    val baseUrl: String
}
