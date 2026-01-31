package org.sake_hack.ui.drawer

/**
 * ドロワーのIntent
 */
sealed interface DrawerIntent {
    /**
     * ドロワーを開く
     */
    data object OpenDrawer : DrawerIntent

    /**
     * ドロワーを閉じる
     */
    data object CloseDrawer : DrawerIntent

    /**
     * 指定されたナビゲーション先に遷移
     *
     * @property destination ナビゲーション先
     */
    data class NavigateTo(val destination: String) : DrawerIntent

    // TODO: 認証機能実装時に追加予定
    // /**
    //  * ユーザーロールを更新
    //  *
    //  * @property role 新しいユーザーロール
    //  */
    // data class UpdateUserRole(val role: UserRole) : DrawerIntent
}
