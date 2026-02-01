package org.sake_hack.feature.auth.presentation

/**
 * ログアウト機能のユーザーアクション
 */
sealed interface LogoutIntent {
    /**
     * ログアウトを実行
     */
    data object Logout : LogoutIntent

    /**
     * エラーをクリア
     */
    data object ClearError : LogoutIntent
}
