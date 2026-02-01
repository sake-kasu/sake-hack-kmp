package org.sake_hack.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.sake_hack.feature.auth.presentation.LogoutIntent
import org.sake_hack.feature.auth.presentation.LogoutViewModel
import org.sake_hack.ui.components.CommonAppBar
import org.sake_hack.ui.components.NavigationDrawer
import org.sake_hack.ui.drawer.DrawerIntent
import org.sake_hack.ui.drawer.DrawerViewModel
import org.sake_hack.ui.login.LoginScreen
import org.sake_hack.ui.logout.LogoutDialog
import org.sake_hack.ui.sakelist.SakeListScreen
import org.sake_hack.ui.stocklist.StockListScreen

@Composable
fun SakeAppNavGraph(
    startDestination: String = NavDestinations.STOCK_LIST,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val drawerViewModel: DrawerViewModel = koinViewModel()
    val logoutViewModel: LogoutViewModel = koinViewModel()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val drawerUiState by drawerViewModel.uiState.collectAsState()
    val logoutUiState by logoutViewModel.uiState.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }

    // ログアウト成功時の処理
    LaunchedEffect(logoutUiState.isLogoutSuccess) {
        if (logoutUiState.isLogoutSuccess) {
            showLogoutDialog = false
            navController.navigate(NavDestinations.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // ドロワー開閉のIntent連携
    LaunchedEffect(drawerState.currentValue) {
        if (drawerState.currentValue == DrawerValue.Open) {
            drawerViewModel.handleIntent(DrawerIntent.OpenDrawer)
        } else {
            drawerViewModel.handleIntent(DrawerIntent.CloseDrawer)
        }
    }

    Box {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                NavigationDrawer(
                    uiState = drawerUiState,
                    currentUser = null, // TODO: Phase 8でGetCurrentUserUseCaseから取得
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
                    onLogout = {
                        showLogoutDialog = true
                        scope.launch { drawerState.close() }
                    }
                )
            }
        ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
        ) {
            // ログイン画面
            composable(NavDestinations.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(NavDestinations.STOCK_LIST) {
                            popUpTo(NavDestinations.LOGIN) { inclusive = true }
                        }
                    }
                )
            }
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

        // ログアウトダイアログ
        if (showLogoutDialog) {
            LogoutDialog(
                onDismiss = { showLogoutDialog = false },
                onConfirm = {
                    logoutViewModel.handleIntent(LogoutIntent.Logout)
                }
            )
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
