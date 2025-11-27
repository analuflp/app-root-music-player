package com.alf.app_root_music_player.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alf.app_root_music_player.ui.screens.GamePlayerScreen
import com.alf.app_root_music_player.ui.screens.GameSelectionScreen

sealed class Screen(val route: String) {
    object GameSelection : Screen("gameSelection")
    data class GamePlayer(val gameId: String) : Screen("gamePlayer/{gameId}") {
        fun createRoute(gameId: String) = "gamePlayer/$gameId"
    }
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.GameSelection.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.GameSelection.route) {
            GameSelectionScreen(
                onGameSelected = { gameId ->
                    navController.navigate(Screen.GamePlayer("").createRoute(gameId))
                }
            )
        }
        
        composable("gamePlayer/{gameId}") { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
            GamePlayerScreen(
                gameId = gameId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

