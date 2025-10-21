package com.example.gamebugs.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gamebugs.ui.theme.GameBugsTheme

data class Settings(
    var gameDifficult: Int = 3,
    var gameSpeed: Float = 1.0f,
    var maxBeetles: Int = 10,
    var bonusInterval: Int = 15,
    var roundDuration: Int = 60
)

@Composable
fun SettingsPanel(
    usefulSettings: Settings,
    onSavedSettings: (Settings) -> Unit = {}
) {

    var gameSpeed by remember { mutableStateOf(usefulSettings.gameSpeed.toString()) }
    var maxBeetles by remember { mutableStateOf(usefulSettings.maxBeetles.toString()) }
    var bonusInterval by remember { mutableStateOf(usefulSettings.bonusInterval.toString()) }
    var roundDuration by remember { mutableStateOf(usefulSettings.roundDuration.toString()) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Настройки игры",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        SettingItem(
            title = "Скорость игры",
            description = "Коэффициент скорости движения тараканов (0 - 3)",
            value = gameSpeed,
            onValueChange = { gameSpeed = it },
            keyboardType = KeyboardType.Decimal
        )

        SettingItem(
            title = "Максимальное количество тараканов",
            description = "Сколько тараканов может одновременно находиться на экране",
            value = maxBeetles,
            onValueChange = { maxBeetles = it },
            keyboardType = KeyboardType.Number
        )

        SettingItem(
            title = "Интервал появления бонусов",
            description = "Время в секундах между появлением бонусов",
            value = bonusInterval,
            onValueChange = { bonusInterval = it },
            keyboardType = KeyboardType.Number
        )

        SettingItem(
            title = "Длительность раунда",
            description = "Продолжительность раунда в секундах",
            value = roundDuration,
            onValueChange = { roundDuration = it },
            keyboardType = KeyboardType.Number
        )

        Button(
            onClick = {
                val settings = Settings(
                    gameSpeed = gameSpeed.toFloat(),
                    maxBeetles = maxBeetles.toInt(),
                    bonusInterval = bonusInterval.toInt(),
                    roundDuration = roundDuration.toInt()
                )
                onSavedSettings(settings)
            },
            enabled =
                ((gameSpeed.isNotBlank() && gameSpeed.toFloat() > 0 && gameSpeed.toFloat() <= 3)
                    && maxBeetles.isNotBlank()
                    && bonusInterval.isNotBlank()
                    && roundDuration.isNotBlank()),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Сохранить настройки",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    description: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = keyboardType
                ),
                placeholder = {
                    Text("Введите значение")
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewSettingsPanel(){
    GameBugsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val settings = Settings()
            SettingsPanel(settings)
        }
    }
}
