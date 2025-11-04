package com.example.gamebugs.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gamebugs.R
import com.example.gamebugs.dataBase.repository.MockPlayerRepository
import com.example.gamebugs.dataBase.repository.MockRecordsRepository
import com.example.gamebugs.model.Settings
import com.example.gamebugs.model.viewModel.GameViewModel
import com.example.gamebugs.model.viewModel.PlayerViewModel
import com.example.gamebugs.ui.config.Screens
import com.example.gamebugs.ui.theme.GameBugsTheme

@Composable
fun MainMenuPanel(
    navController: NavHostController,
    gameViewModel: GameViewModel,
    playerViewModel: PlayerViewModel,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Регистрация", "Правила", "Рекорды", "Список авторов", "Настройки")
    val existingPlayers by playerViewModel.players.collectAsState(emptyList())
    var isRegistered by rememberSaveable { mutableStateOf(playerViewModel.playerEntity != null) }

    LaunchedEffect(Unit) {
        playerViewModel.loadPlayers()
    }

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 7.dp)
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (isRegistered && playerViewModel.playerEntity != null) {
            Text(
                text = "Игрок: ${playerViewModel.playerEntity!!.name}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }

        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Левая колонка
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(vertical = 10.dp)
            ) {
                Button(
                    onClick = {
                        if (playerViewModel.playerEntity != null) {
                            navController.navigate(Screens.Game.route)
                        }
                    },
                    enabled = playerViewModel.playerEntity != null
                ) {
                    Text(
                        text = "Новая игра",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                tabs.forEachIndexed { index, title ->
                    Text(
                        text = title,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { selectedTab = index },
                        style = if (selectedTab == index) {
                            MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
                        } else {
                            MaterialTheme.typography.bodyLarge
                        }
                    )
                }
            }

            // область справа
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(5.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        RegistrationPanel(
                            onRegisteredPlayer = { newPlayer ->
                                playerViewModel.savePlayer(newPlayer)
                                playerViewModel.playerEntity = newPlayer
                                isRegistered = true
                                playerViewModel.settings.gameDifficult = newPlayer.difficulty
                            },
                            existingPlayers = existingPlayers,
                            onPlayerSelected = { selectedPlayer ->
                                playerViewModel.playerEntity = selectedPlayer
                                isRegistered = true
                                playerViewModel.settings.gameDifficult = selectedPlayer.difficulty
                            },
                            onDeletedPlayer = { deletedPlayer ->
                                playerViewModel.deletePlayer(deletedPlayer)
                            },
                            playerViewModel = playerViewModel
                        )
                    }
                    1 -> RulesPanel()
                    2 -> RecordsPanel(gameViewModel)
                    3 -> AuthorsPanel()
                    4 -> SettingsPanel(
                        playerViewModel.settings,
                        onSavedSettings = { savedSettings ->
                            playerViewModel.settings.copy(savedSettings)
                        }
                    )
                }
            }
        }
    }
}

private fun Settings.copy(newSettings: Settings) {
    this.gameDifficult = newSettings.gameDifficult
    this.gameSpeed = newSettings.gameSpeed
    this.roundDuration = newSettings.roundDuration
    this.bonusInterval = newSettings.bonusInterval
    this.maxBeetles = newSettings.maxBeetles
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
fun PreviewMainMenuPanel(){
    GameBugsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ){
            val gameViewModel = GameViewModel(MockRecordsRepository())
            val playerViewModel = PlayerViewModel(MockPlayerRepository())

            LaunchedEffect(Unit) {
                playerViewModel.loadPlayers()
            }

            val navController = rememberNavController()

            MainMenuPanel(
                navController = navController,
                gameViewModel = gameViewModel,
                playerViewModel = playerViewModel
            )
        }
    }
}
