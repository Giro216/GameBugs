import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gamebugs.R
import com.example.gamebugs.ui.components.AuthorsPanel
import com.example.gamebugs.ui.components.Player
import com.example.gamebugs.ui.components.RegistrationPanel
import com.example.gamebugs.ui.components.RulesPanel
import com.example.gamebugs.ui.components.Settings
import com.example.gamebugs.ui.components.SettingsPanel
import com.example.gamebugs.ui.config.Screens

@Composable
fun MainMenuPanel(
    navController: NavHostController,
    player: Player?,
    settings: Settings?,
    onPlayerUpdated: (Player?) -> Unit,
    onSettingsUpdated: (Settings?) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Регистрация", "Правила", "Список авторов", "Настройки")
    var isRegistered by remember { mutableStateOf(player != null) }

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 7.dp, vertical = 45.dp)
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
                modifier = Modifier.padding(vertical = 20.dp)
            ) {
                Button(
                    onClick = {
                        if (player != null && settings != null) {
                            navController.navigate(Screens.Game.route)
                        }
                    },
                    enabled = player != null && settings != null
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
                    .padding(10.dp)
            ) {
                when (selectedTab) {
                    0 -> RegistrationPanel(
                        onRegisteredPlayer = { registeredPlayer ->
                            onPlayerUpdated(registeredPlayer)
                            isRegistered = true
                        }
                    )
                    1 -> RulesPanel()
                    2 -> AuthorsPanel()
                    3 -> SettingsPanel(
                        onSavedSettings = { savedSettings ->
                            onSettingsUpdated(savedSettings)
                        }
                    )
                }
            }
        }
    }
}