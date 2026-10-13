package com.example.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.app.presentation.screens.home.HomeScreen
import com.example.app.presentation.screens.library.LibraryScreen
import com.example.app.presentation.screens.player.PlayerScreen
import com.example.app.presentation.screens.settings.SettingsScreen

@Composable
fun AudioBookNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Route.Home) {
        composable<Route.Home> {
            HomeScreen(
                onOpenPlayer = { id ->
                    navController.navigate(Route.Player(id)) {
                        launchSingleTop = true
                    }
                },
                onOpenLibrary = { navController.navigate(Route.Library) },
                onOpenSettings = { navController.navigate(Route.Settings) },
            )
        }
        composable<Route.Library> {
            LibraryScreen(
                onOpenPlayer = { id -> navController.navigate(Route.Player(id)) },
            )
        }
        composable<Route.Player> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Player>()
            PlayerScreen(onBack = { navController.popBackStack() })
        }
        composable<Route.Settings> {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
