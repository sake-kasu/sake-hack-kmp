package org.sake_hack.feature.auth.domain.repository

import org.sake_hack.feature.auth.domain.model.User

/**
 * 認証機能のRepositoryインターフェース
 */
interface AuthRepository {
    /**
     * Googleアカウントでログイン
     */
    suspend fun loginWithGoogle(): Result<User>

    /**
     * ゲストとしてログイン
     */
    suspend fun loginAsGuest(): Result<User>

    /**
     * ログアウト
     */
    suspend fun logout(): Result<Unit>

    /**
     * 現在ログインしているユーザーを取得
     */
    suspend fun getCurrentUser(): Result<User?>

    /**
     * IDトークンを取得
     */
    suspend fun getIdToken(): Result<String?>

    /**
     * セッションが有効かどうかを確認
     */
    suspend fun isSessionValid(): Result<Boolean>
}
