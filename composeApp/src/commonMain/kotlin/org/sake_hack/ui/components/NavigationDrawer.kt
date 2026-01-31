package org.sake_hack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sake_hack.domain.model.NavDestination
import org.sake_hack.domain.model.NavigationPermission
import org.sake_hack.navigation.NavDestinations
import org.sake_hack.ui.drawer.DrawerIntent
import org.sake_hack.ui.drawer.DrawerUiState

/**
 * ナビゲーションドロワーコンポーネント
 *
 * デザイン仕様: docs/screens/common.pen (ID: 3fAbU)
 *
 * @param uiState ドロワーのUI状態
 * @param onIntent Intentハンドラー
 * @param onNavigate ナビゲーションハンドラー
 * @param modifier Modifier
 */
@Composable
fun NavigationDrawer(
    uiState: DrawerUiState,
    onIntent: (DrawerIntent) -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // デザイン仕様: width=300dp, backgroundColor=$white, shadow
    Column(
        modifier = modifier
            .width(300.dp)
            .fillMaxHeight()
            .background(Color.White)
            .shadow(
                elevation = 16.dp,
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .windowInsetsPadding(WindowInsets.statusBars) // ステータスバー領域を回避
    ) {
        // ヘッダー
        DrawerHeader()

        // メニューセクション
        MenuSection(
            currentDestination = uiState.currentDestination,
            navigationPermission = uiState.navigationPermission,
            onMenuItemClick = { destination ->
                onIntent(DrawerIntent.NavigateTo(destination))
                onNavigate(destination)
            }
        )
    }
}

/**
 * ドロワーヘッダーコンポーネント
 *
 * デザイン仕様: docs/screens/common.pen (ID: CK6oC)
 */
@Composable
private fun DrawerHeader() {
    // デザイン仕様: padding=[24, 16], gap=4dp, border-bottom
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "酒アプリ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default, // TODO: Noto Sans JP
                color = Color(0xFF1A1A1A) // $text-body
            )
        }
        // border-bottom
        androidx.compose.material3.HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color(0xFFE0E0E0) // $border-divider
        )
    }
}

/**
 * メニューセクションコンポーネント
 *
 * デザイン仕様: docs/screens/common.pen (ID: mhUYv)
 *
 * @param currentDestination 現在選択中のナビゲーション先
 * @param navigationPermission ナビゲーション権限
 * @param onMenuItemClick メニュー項目クリックハンドラー
 */
@Composable
private fun MenuSection(
    currentDestination: String,
    navigationPermission: NavigationPermission,
    onMenuItemClick: (String) -> Unit
) {
    // デザイン仕様: padding=8dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // セクションラベル
        Text(
            text = "メニュー",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Default, // TODO: Noto Sans JP
            color = Color(0xFF757575), // $text-description
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 酒一覧メニュー（ロールベース表示制御）
        if (navigationPermission.canAccess(NavDestination.SakeList)) {
            DrawerMenuItem(
                icon = Icons.Default.LocalBar,
                label = "酒一覧",
                isSelected = currentDestination == NavDestinations.SAKE_LIST,
                onClick = { onMenuItemClick(NavDestinations.SAKE_LIST) }
            )
        }

        // 在庫一覧メニュー（ロールベース表示制御）
        if (navigationPermission.canAccess(NavDestination.StockList)) {
            DrawerMenuItem(
                icon = Icons.Default.Inventory2,
                label = "在庫一覧",
                isSelected = currentDestination == NavDestinations.STOCK_LIST,
                onClick = { onMenuItemClick(NavDestinations.STOCK_LIST) }
            )
        }
    }
}

/**
 * ドロワーメニュー項目コンポーネント
 *
 * デザイン仕様: docs/screens/common.pen (ID: JlveO, HETfn)
 *
 * @param icon アイコン
 * @param label ラベル
 * @param isSelected 選択状態
 * @param onClick クリックハンドラー
 * @param modifier Modifier
 */
@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // デザイン仕様: padding=[12, 16], gap=12dp, cornerRadius=8dp
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                color = if (isSelected) Color(0xFFE3F2FD) else Color.Transparent, // $sea-50
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = if (isSelected) Color(0xFF1976D2) else Color(0xFF1A1A1A) // $button-normal / $text-body
        )

        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontFamily = FontFamily.Default, // TODO: Noto Sans JP
            color = if (isSelected) Color(0xFF1976D2) else Color(0xFF1A1A1A) // $button-normal / $text-body
        )
    }
}
