package org.sake_hack.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 共通ヘッダーコンポーネント
 *
 * デザイン仕様:
 * - height: 64dp
 * - メニューアイコン: 24dp
 * - タイトル: 20sp, FontWeight.Bold
 * - gap: 16dp (メニューアイコンとタイトル間)
 *
 * @param title 画面タイトル
 * @param onMenuClick メニューアイコンクリック時の処理
 * @param modifier Modifier
 * @param actions 画面固有のアクションボタンスロット
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonAppBar(
    title: String,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = "メニュー",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        modifier = modifier
    )
}
