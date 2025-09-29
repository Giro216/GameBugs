package com.example.gamebugs.ui.config

import MainMenuPanel
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gamebugs.ui.components.GameHandler
import com.example.gamebugs.ui.components.SettingsPanel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.MainMenu.route
    ) {
        composable(Screens.MainMenu.route) {
            MainMenuPanel(navController = navController)
        }
        composable(Screens.Game.route) {
            GameHandler(navController = navController)
        }
//        composable(Screens.Settings.route) {
//            SettingsPanel(navController = navController)
//        }
    }
}

// Определение экранов
sealed class Screens(val route: String) {
    object MainMenu : Screens("main_menu")
    object Game : Screens("game_screen")
    object Settings : Screens("settings")
}