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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gamebugs.R
import com.example.gamebugs.dataBase.model.PlayerEntity
import com.example.gamebugs.dataBase.model.viewModel.GameViewModel
import com.example.gamebugs.dataBase.model.viewModel.PlayerViewModel
import com.example.gamebugs.dataBase.repository.MockPlayerRepository
import com.example.gamebugs.dataBase.repository.MockRecordsRepository
import com.example.gamebugs.ui.config.Screens
import com.example.gamebugs.ui.theme.GameBugsTheme

@Composable
fun MainMenuPanel(
    navController: NavHostController,
    player: PlayerEntity?,
    settings: Settings,
    onPlayerUpdated: (PlayerEntity?) -> Unit,
    gameViewModel: GameViewModel,
    playerViewModel: PlayerViewModel,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Регистрация", "Правила", "Рекорды", "Список авторов", "Настройки")
    val existingPlayers by playerViewModel.players.collectAsState(emptyList())
    var isRegistered by remember { mutableStateOf(player != null) }

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

        if (isRegistered && player != null) {
            Text(
                text = "Игрок: ${player.name}",
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
                        if (player != null) {
                            navController.navigate(Screens.Game.route)
                        }
                    },
                    enabled = player != null
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
                                onPlayerUpdated(newPlayer)
                                isRegistered = true
                                settings.gameDifficult = newPlayer.difficulty
                            },
                            existingPlayers = existingPlayers,
                            onPlayerSelected = { selectedPlayer ->
                                onPlayerUpdated(selectedPlayer)
                                isRegistered = true
                                settings.gameDifficult = selectedPlayer.difficulty
                            }
                        )
                    }
                    1 -> RulesPanel()
                    2 -> RecordsPanel(gameViewModel)
                    3 -> AuthorsPanel()
                    4 -> SettingsPanel(
                        settings,
                        onSavedSettings = { savedSettings ->
                            settings.copy(savedSettings)
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
                player = null,
                settings = Settings(),
                onPlayerUpdated = {},
                gameViewModel = gameViewModel,
                playerViewModel = playerViewModel
            )
        }
    }
}
