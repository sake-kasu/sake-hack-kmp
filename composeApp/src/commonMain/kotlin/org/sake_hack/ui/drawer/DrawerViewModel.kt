package org.sake_hack.ui.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ドロワーのViewModel
 *
 * TODO: 認証機能実装時に、GetCurrentUserUseCaseをDIして初期化時にユーザーロールを取得
 */
class DrawerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DrawerUiState())
    val uiState: StateFlow<DrawerUiState> = _uiState.asStateFlow()

    /**
     * Intentを処理
     *
     * @param intent ドロワーのIntent
     */
    fun handleIntent(intent: DrawerIntent) {
        when (intent) {
            is DrawerIntent.OpenDrawer -> {
                _uiState.update { it.copy(isDrawerOpen = true) }
            }
            is DrawerIntent.CloseDrawer -> {
                _uiState.update { it.copy(isDrawerOpen = false) }
            }
            is DrawerIntent.NavigateTo -> {
                _uiState.update {
                    it.copy(
                        currentDestination = intent.destination,
                        isDrawerOpen = false
                    )
                }
            }
        }
    }

    // TODO: 認証機能実装時に追加
    // /**
    //  * ユーザーロールを更新
    //  *
    //  * @param role 新しいユーザーロール
    //  */
    // fun updateUserRole(role: UserRole) {
    //     _uiState.update {
    //         it.copy(
    //             userRole = role,
    //             navigationPermission = NavigationPermission.forRole(role)
    //         )
    //     }
    // }
    //
    // /**
    //  * 認証API実装後に追加予定
    //  * 初期化時にユーザーロールをロード
    //  */
    // private fun loadUserRole() {
    //     viewModelScope.launch {
    //         getCurrentUserUseCase()
    //             .onSuccess { user ->
    //                 _uiState.update {
    //                     it.copy(
    //                         userRole = user.role,
    //                         navigationPermission = NavigationPermission.forRole(user.role)
    //                     )
    //                 }
    //             }
    //             .onFailure { error ->
    //                 // エラー時はGuestロールにフォールバック
    //                 _uiState.update {
    //                     it.copy(
    //                         userRole = UserRole.Guest,
    //                         navigationPermission = NavigationPermission.forRole(UserRole.Guest)
    //                     )
    //                 }
    //             }
    //     }
    // }
}
