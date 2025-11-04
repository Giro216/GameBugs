package com.example.gamebugs.ui.config

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gamebugs.model.viewModel.CurrencyViewModel
import com.example.gamebugs.model.viewModel.GameViewModel
import com.example.gamebugs.model.viewModel.PlayerViewModel
import com.example.gamebugs.ui.components.GameHandler
import com.example.gamebugs.ui.components.MainMenuPanel

@Composable
fun AppNavigation(
    gameViewModel: GameViewModel,
    playerViewModel: PlayerViewModel,
    currencyViewModel: CurrencyViewModel
) {
    val navController = rememberNavController()

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
                playerViewModel = playerViewModel
            )
        }

        composable(Screens.Game.route) {
            if (playerViewModel.playerEntity != null) {
                GameHandler(
                    navController = navController,
                    gameViewModel = gameViewModel,
                    playerViewModel = playerViewModel,
                    currencyViewModel = currencyViewModel
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