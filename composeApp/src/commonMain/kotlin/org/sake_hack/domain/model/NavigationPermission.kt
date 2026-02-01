package org.sake_hack.domain.model

import org.sake_hack.core.common.domain.model.UserRole

/**
 * ナビゲーション先の識別子
 */
enum class NavDestination {
    SakeList,
    StockList,
    Settings
}

/**
 * ナビゲーション権限モデル
 *
 * TODO: 認証機能実装時に、バックエンドからロールごとの権限情報を取得
 *
 * @property allowedDestinations アクセス可能なナビゲーション先のセット
 * @property userRole ユーザーロール
 */
data class NavigationPermission(
    val allowedDestinations: Set<NavDestination>,
    val userRole: UserRole
) {
    /**
     * 指定されたナビゲーション先にアクセス可能かを判定
     *
     * @param destination ナビゲーション先
     * @return アクセス可能な場合はtrue
     */
    fun canAccess(destination: NavDestination): Boolean =
        destination in allowedDestinations

    companion object {
        /**
         * ロールに基づいてNavigationPermissionを生成
         *
         * TODO: 将来的にはバックエンドから権限マッピングを動的に取得
         *       API: GET /api/permissions?role=admin
         *
         * @param role ユーザーロール
         * @return ロールに対応するNavigationPermission
         */
        fun forRole(role: UserRole): NavigationPermission = when (role) {
            UserRole.Admin -> NavigationPermission(
                allowedDestinations = setOf(
                    NavDestination.SakeList,
                    NavDestination.StockList,
                    NavDestination.Settings
                ),
                userRole = role
            )
            UserRole.Manager -> NavigationPermission(
                allowedDestinations = setOf(
                    NavDestination.SakeList,
                    NavDestination.StockList
                ),
                userRole = role
            )
            UserRole.User -> NavigationPermission(
                allowedDestinations = setOf(
                    NavDestination.SakeList
                ),
                userRole = role
            )
            UserRole.Guest -> NavigationPermission(
                allowedDestinations = emptySet(),
                userRole = role
            )
        }
    }
}
