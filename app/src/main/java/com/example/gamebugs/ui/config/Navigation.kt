package com.example.gamebugs.ui.config

import MainMenuPanel
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gamebugs.ui.components.GameHandler
import com.example.gamebugs.ui.components.Player
import com.example.gamebugs.ui.components.Settings

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var player by remember { mutableStateOf<Player?>(null) }
    var settings by remember { mutableStateOf(Settings()) }

    BackHandler(enabled = true) {
        when (navController.currentDestination?.route) {
            Screens.Game.route -> {
                return@BackHandler
            }
            else -> {
                if (!navController.popBackStack()) {

                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screens.MainMenu.route
    ) {
        composable(Screens.MainMenu.route) {
            MainMenuPanel(
                navController = navController,
                player = player,
                settings = settings,
                onPlayerUpdated = { newPlayer -> player = newPlayer },
            )
        }

        composable(Screens.Game.route) {
            if (player != null) {
                GameHandler(
                    navController = navController,
                    settings = settings,
                    player = player!!
                )
            }else {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
    }
}

// Определение экранов
sealed class Screens(val route: String) {
    object MainMenu : Screens("main_menu")
    object Game : Screens("game_screen")
}