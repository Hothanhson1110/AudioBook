package com.example.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.app.presentation.screens.home.HomeScreen
import com.example.app.presentation.screens.library.LibraryScreen
import com.example.app.presentation.screens.reader.ReaderScreen

@Composable
fun AudioBookNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Route.Home) {
        composable<Route.Home> {
            HomeScreen(
                onOpenReader = { id -> navController.navigate(Route.Reader(id)) },
                onOpenLibrary = { navController.navigate(Route.Library) }
            )
        }
        composable<Route.Library> { LibraryScreen() }
        composable<Route.Reader> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Reader>()
            ReaderScreen(bookId = route.bookId, onBack = navController::popBackStack)
        }
    }
}