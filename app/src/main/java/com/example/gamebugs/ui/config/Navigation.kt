package com.example.gamebugs.ui.config

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
import com.example.gamebugs.dataBase.model.PlayerEntity
import com.example.gamebugs.model.viewModel.CurrencyViewModel
import com.example.gamebugs.model.viewModel.GameViewModel
import com.example.gamebugs.model.viewModel.PlayerViewModel
import com.example.gamebugs.ui.components.GameHandler
import com.example.gamebugs.ui.components.MainMenuPanel
import com.example.gamebugs.ui.components.Settings

@Composable
fun AppNavigation(gameViewModel: GameViewModel, playerViewModel: PlayerViewModel) {
    val navController = rememberNavController()
    var playerEntity by remember { mutableStateOf<PlayerEntity?>(null) }
    var settings by remember { mutableStateOf(Settings()) }


    BackHandler(enabled = true) {
        when (navController.currentDestination?.route) {
            Screens.Game.route -> {
                return@BackHandler
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
                gameViewModel = gameViewModel,
                playerViewModel = playerViewModel,
                player = playerEntity,
                settings = settings,
                onPlayerUpdated = { newPlayer -> playerEntity = newPlayer },
            )
        }

        composable(Screens.Game.route) {
            if (playerEntity != null) {
                GameHandler(
                    navController = navController,
                    gameViewModel = gameViewModel,
                    playerViewModel = playerViewModel,
                    settings = settings,
                    player = playerEntity!!
                )
            }else {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
    }
}

sealed class Screens(val route: String) {
    object MainMenu : Screens("main_menu")
    object Game : Screens("game_screen")
}