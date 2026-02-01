package org.sake_hack.core.common.domain.model

/**
 * ユーザーロールを表すsealed interface
 *
 * TODO: 認証機能実装時に、バックエンドから取得したロール文字列をUserRoleに変換
 */
sealed interface UserRole {
    /**
     * 管理者ロール
     * - 全機能へのアクセス権限
     */
    data object Admin : UserRole

    /**
     * マネージャーロール
     * - 酒一覧、在庫一覧へのアクセス権限
     */
    data object Manager : UserRole

    /**
     * 一般ユーザーロール
     * - 酒一覧へのアクセス権限のみ
     */
    data object User : UserRole

    /**
     * ゲストロール
     * - アクセス権限なし
     */
    data object Guest : UserRole

    companion object {
        /**
         * 文字列からUserRoleに変換
         *
         * TODO: 認証機能実装時に、バックエンドAPIのレスポンスから取得したロール文字列を変換
         *
         * @param role ロール文字列 (例: "admin", "manager", "user")
         * @return 対応するUserRole (該当なしの場合はGuest)
         */
        fun fromString(role: String): UserRole = when (role.lowercase()) {
            "admin" -> Admin
            "manager" -> Manager
            "user" -> User
            else -> Guest
        }
    }
}
