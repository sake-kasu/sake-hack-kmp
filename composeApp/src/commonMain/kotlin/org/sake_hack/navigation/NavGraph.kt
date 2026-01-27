package org.sake_hack.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.sake_hack.ui.sakelist.SakeListScreen
import org.sake_hack.ui.stocklist.StockListScreen

@Composable
fun SakeAppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
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
                }
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
                }
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
                }
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
                }
            )
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        bottomBar = {
            org.sake_hack.ui.sakelist.components.SakeBottomNavigation(
                selectedRoute = when (title) {
                    "ホーム" -> NavDestinations.HOME
                    "設定" -> NavDestinations.SETTINGS
                    else -> ""
                },
                onNavigate = onNavigate
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
