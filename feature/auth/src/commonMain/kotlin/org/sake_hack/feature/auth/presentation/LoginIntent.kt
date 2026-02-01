package org.sake_hack.feature.auth.presentation

/**
 * ログイン画面のユーザーアクション
 */
sealed interface LoginIntent {
    /**
     * Googleアカウントでログイン
     */
    data object LoginWithGoogle : LoginIntent

    /**
     * ゲストとしてログイン
     */
    data object LoginAsGuest : LoginIntent

    /**
     * エラーをクリア
     */
    data object ClearError : LoginIntent
}
