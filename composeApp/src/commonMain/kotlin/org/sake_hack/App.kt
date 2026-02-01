package org.sake_hack

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.sake_hack.feature.auth.presentation.SessionViewModel
import org.sake_hack.navigation.NavDestinations
import org.sake_hack.navigation.SakeAppNavGraph

@Composable
@Preview
fun App() {
    val sessionViewModel: SessionViewModel = koinViewModel()
    val sessionState by sessionViewModel.sessionState.collectAsState()

    LaunchedEffect(Unit) {
        sessionViewModel.checkSession()
    }

    MaterialTheme {
        when {
            sessionState.isLoading -> {
                // ローディング表示
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            sessionState.isSessionValid -> {
                // セッション有効 → 在庫一覧画面
                SakeAppNavGraph(startDestination = NavDestinations.STOCK_LIST)
            }
            else -> {
                // セッション無効 → ログイン画面
                SakeAppNavGraph(startDestination = NavDestinations.LOGIN)
            }
        }
    }
}
