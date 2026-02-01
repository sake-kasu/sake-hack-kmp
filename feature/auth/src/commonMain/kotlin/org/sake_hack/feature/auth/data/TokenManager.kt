package org.sake_hack.feature.auth.data

/**
 * IDトークンを管理するクラス（expect/actual）
 */
expect class TokenManager {
    /**
     * トークンを保存
     */
    suspend fun saveToken(token: String)

    /**
     * トークンを取得
     */
    suspend fun getToken(): String?

    /**
     * トークンを削除
     */
    suspend fun clearToken()
}
