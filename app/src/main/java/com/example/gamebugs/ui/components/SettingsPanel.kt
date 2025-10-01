package com.example.gamebugs.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

data class Settings(
    var gameSpeed: Float,
    var maxBeetles: Int,
    var bonusInterval: Int,
    var roundDuration: Int
)

@Composable
fun SettingsPanel(
    onSavedSettings: (Settings) -> Unit = {} // Callback
) {
    // Состояния для хранения значений настроек
    var gameSpeed by remember { mutableStateOf("1.0") }
    var maxBeetles by remember { mutableStateOf("10") }
    var bonusInterval by remember { mutableStateOf("15") }
    var roundDuration by remember { mutableStateOf("60") }


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
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        SettingItem(
            title = "Скорость игры",
            description = "Коэффициент скорости движения тараканов (например: 1.0 - нормальная, 2.0 - в 2 раза быстрее)",
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
                if (gameSpeed.isNotBlank() && maxBeetles.isNotBlank() && bonusInterval.isNotBlank() && roundDuration.isNotBlank()) {
                    val settings = Settings(
                        gameSpeed.toFloat(),
                        maxBeetles.toInt(),
                        bonusInterval.toInt(),
                        roundDuration.toInt()
                    )
                    onSavedSettings(settings)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Сохранить настройки")
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
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