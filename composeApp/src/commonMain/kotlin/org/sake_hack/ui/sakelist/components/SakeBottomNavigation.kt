package org.sake_hack.ui.sakelist.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalBar
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SakeBottomNavigation(
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Home, contentDescription = "ホーム") },
            label = { Text("ホーム") },
            selected = selectedRoute == "home",
            onClick = { onNavigate("home") }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Rounded.LocalBar, contentDescription = "一覧") },
            label = { Text("一覧") },
            selected = selectedRoute == "sake_list",
            onClick = { onNavigate("sake_list") }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Settings, contentDescription = "設定") },
            label = { Text("設定") },
            selected = selectedRoute == "settings",
            onClick = { onNavigate("settings") }
        )
    }
}
