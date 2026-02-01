package org.sake_hack.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.sake_hack.ui.components.CommonAppBar
import org.sake_hack.ui.components.NavigationDrawer
import org.sake_hack.ui.drawer.DrawerIntent
import org.sake_hack.ui.drawer.DrawerViewModel
import org.sake_hack.ui.sakelist.SakeListScreen
import org.sake_hack.ui.stocklist.StockListScreen

@Composable
fun SakeAppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val drawerViewModel: DrawerViewModel = koinViewModel()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val drawerUiState by drawerViewModel.uiState.collectAsState()

    // ドロワー開閉のIntent連携
    LaunchedEffect(drawerState.currentValue) {
        if (drawerState.currentValue == DrawerValue.Open) {
            drawerViewModel.handleIntent(DrawerIntent.OpenDrawer)
        } else {
            drawerViewModel.handleIntent(DrawerIntent.CloseDrawer)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawer(
                uiState = drawerUiState,
                currentUser = null, // TODO: Phase 7で現在のユーザー情報を取得
                onIntent = drawerViewModel::handleIntent,
                onNavigate = { destination ->
                    navController.navigate(destination) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    scope.launch { drawerState.close() }
                },
                onLogout = {} // TODO: Phase 7でログアウト処理を実装
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = NavDestinations.STOCK_LIST,
            modifier = modifier
        ) {
            composable(NavDestinations.SAKE_LIST) {
                SakeListScreen(
                    onNavigate = { route ->
                        navController.navigate(route) {
                            // Pop up to start destination to avoid building up a large back stack
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    },
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }

            composable(NavDestinations.STOCK_LIST) {
                StockListScreen(
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }

            composable(NavDestinations.HOME) {
                PlaceholderScreen(
                    title = "ホーム",
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }

            composable(NavDestinations.SETTINGS) {
                PlaceholderScreen(
                    title = "設定",
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    onNavigate: (String) -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CommonAppBar(
                title = title,
                onMenuClick = onMenuClick
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "この画面は開発中です",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
