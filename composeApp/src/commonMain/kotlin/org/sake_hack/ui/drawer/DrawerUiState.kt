package org.sake_hack.ui.drawer

import org.sake_hack.core.common.domain.model.UserRole
import org.sake_hack.domain.model.NavigationPermission
import org.sake_hack.navigation.NavDestinations

/**
 * ドロワーのUI状態
 *
 * @property currentDestination 現在選択中のナビゲーション先
 * @property userRole ユーザーロール (TODO: 認証実装後、実際のユーザーロールを取得)
 * @property navigationPermission ナビゲーション権限
 * @property isDrawerOpen ドロワーの開閉状態
 */
data class DrawerUiState(
    val currentDestination: String = NavDestinations.STOCK_LIST,
    val userRole: UserRole = UserRole.Admin, // TODO: 認証実装後、実際のユーザーロールを取得（現在はテスト用にAdmin）
    val navigationPermission: NavigationPermission = NavigationPermission.forRole(UserRole.Admin),
    val isDrawerOpen: Boolean = false
)
