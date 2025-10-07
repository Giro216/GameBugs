package com.example.gamebugs.ui.components

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gamebugs.R
import com.example.gamebugs.dataBase.model.PlayerEntity
import com.example.gamebugs.ui.theme.GameBugsTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("SimpleDateFormat")
@Composable
fun RegistrationPanel(
    onRegisteredPlayer: (PlayerEntity) -> Unit = {},
    existingPlayers: List<PlayerEntity> = emptyList(),
    onPlayerSelected: (PlayerEntity) -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf<RegistrationScreen>(RegistrationScreen.Start) }
    var selectedExistingPlayer by remember { mutableStateOf<PlayerEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        when (currentScreen) {
            RegistrationScreen.Start -> {
                StartScreen(
                    existingPlayers = existingPlayers,
                    onSelectExisting = { currentScreen = RegistrationScreen.SelectExisting },
                    onCreateNew = { currentScreen = RegistrationScreen.CreateNew }
                )
            }

            RegistrationScreen.SelectExisting -> {
                SelectExistingScreen(
                    existingPlayers = existingPlayers,
                    onPlayerSelected = { player ->
                        selectedExistingPlayer = player
                        onPlayerSelected(player)
                        currentScreen = RegistrationScreen.Start
                    },
                    onBack = { currentScreen = RegistrationScreen.Start }
                )
            }

            RegistrationScreen.CreateNew -> {
                CreateNewScreen(
                    onRegisteredPlayer = { player ->
                        onRegisteredPlayer(player)
                        currentScreen = RegistrationScreen.Start
                    },
                    onBack = { currentScreen = RegistrationScreen.Start }
                )
            }
        }

        selectedExistingPlayer?.let { player ->
            Spacer(modifier = Modifier.height(16.dp))
            PlayerInfoCard(player = player)
        }
    }
}

@Composable
private fun StartScreen(
    existingPlayers: List<PlayerEntity>,
    onSelectExisting: () -> Unit,
    onCreateNew: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(vertical = 32.dp)
    ) {
        Text(
            "Выберите действие",
            style = MaterialTheme.typography.headlineMedium
        )

        if (existingPlayers.isNotEmpty()) {
            Button(
                onClick = onSelectExisting,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Выбрать существующий профиль")
            }
        }

        Button(
            onClick = onCreateNew,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Зарегистрироваться")
        }

        if (existingPlayers.isNotEmpty()) {
            Text(
                "Доступно профилей: ${existingPlayers.size}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SelectExistingScreen(
    existingPlayers: List<PlayerEntity>,
    onPlayerSelected: (PlayerEntity) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_media_previous),
                    contentDescription = "Назад"
                )
            }

            Text(
                "Выберите игрока",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Поиск по имени") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        val filteredPlayers = if (searchQuery.isBlank()) {
            existingPlayers
        } else {
            existingPlayers.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }

        if (filteredPlayers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (searchQuery.isBlank()) "Нет сохраненных игроков" else "Игроки не найдены",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredPlayers) { player ->
                    PlayerSelectionCard(
                        player = player,
                        onSelected = { onPlayerSelected(player) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateNewScreen(
    onRegisteredPlayer: (PlayerEntity) -> Unit,
    onBack: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var difficulty by remember { mutableFloatStateOf(1f) }
    var birthDate by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day)
            birthDate = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Button(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_media_previous),
                        contentDescription = "Назад"
                    )
                }

                Text(
                    "Регистрация нового игрока",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        item {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Введите ФИО") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        item {
            Text("Пол:",
                fontSize = 18.sp,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            Row {
                listOf("Муж", "Жен").forEach { option ->
                    Row(
                        modifier = Modifier
                            .selectable(
                                selected = (gender == option),
                                onClick = { gender = option }
                            )
                            .padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (gender == option),
                            onClick = { gender = option }
                        )
                        Text(text = option,
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        item {
            var expanded by remember { mutableStateOf(false) }
            var selectedCourse by remember { mutableStateOf("Выберите курс") }

            Box(modifier = Modifier.padding(vertical = 8.dp)) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedCourse)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("1 курс", "2 курс", "3 курс", "4 курс").forEach { courseOption ->
                        DropdownMenuItem(
                            text = { Text(courseOption) },
                            onClick = {
                                course = courseOption
                                selectedCourse = courseOption
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        item {
            Text("Сложность: ${difficulty.toInt()}")
            Slider(
                value = difficulty,
                onValueChange = { difficulty = it },
                steps = 3,
                valueRange = 1f..5f,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedButton(
                onClick = { datePickerDialog.show() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "Выбрать дату рождения")
            }
        }

        item {
            Text(
                "Дата: ${SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(birthDate))}",
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            Button(
                onClick = {
                    if (fullName.isNotBlank() && gender.isNotBlank() && course.isNotBlank()) {
                        val zodiac = getZodiac(birthDate)
                        val player = PlayerEntity(
                            name = fullName,
                            gender = gender,
                            course = course,
                            difficulty = difficulty.toInt(),
                            birthDate = birthDate,
                            zodiac = zodiac
                        )
                        onRegisteredPlayer(player)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = fullName.isNotBlank() && gender.isNotBlank() && course.isNotBlank()
            ) {
                Text("Зарегистрироваться")
            }
        }
    }
}

@Composable
private fun PlayerInfoCard(player: PlayerEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Текущий игрок:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                player.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Пол: ${player.gender}", style = MaterialTheme.typography.bodyMedium)
                Text("Курс: ${player.course}", style = MaterialTheme.typography.bodyMedium)
                Text("Сложность: ${player.difficulty}", style = MaterialTheme.typography.bodyMedium)
            }

            Text(
                "Знак зодиака: ${player.zodiac}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun PlayerSelectionCard(
    player: PlayerEntity,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelected),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = player.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Пол: ${player.gender}", style = MaterialTheme.typography.bodyMedium)
                Text("Курс: ${player.course}", style = MaterialTheme.typography.bodyMedium)
                Text("Сложность: ${player.difficulty}", style = MaterialTheme.typography.bodyMedium)
            }

            Text(
                "Знак зодиака: ${player.zodiac}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

sealed class RegistrationScreen {
    object Start : RegistrationScreen()
    object SelectExisting : RegistrationScreen()
    object CreateNew : RegistrationScreen()
}

fun getZodiac(dateMillis: Long): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = dateMillis
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val month = cal.get(Calendar.MONTH) + 1
    return when {
        (day >= 21 && month == 3) || (day <= 20 && month == 4) -> "Овен"
        (day >= 21 && month == 4) || (day <= 20 && month == 5) -> "Телец"
        (day >= 21 && month == 5) || (day <= 21 && month == 6) -> "Близнецы"
        (day >= 22 && month == 6) || (day <= 22 && month == 7) -> "Рак"
        (day >= 23 && month == 7) || (day <= 22 && month == 8) -> "Лев"
        (day >= 23 && month == 8) || (day <= 22 && month == 9) -> "Дева"
        (day >= 23 && month == 9) || (day <= 23 && month == 10) -> "Весы"
        (day >= 24 && month == 10) || (day <= 22 && month == 11) -> "Скорпион"
        (day >= 23 && month == 11) || (day <= 21 && month == 12) -> "Стрелец"
        (day >= 22 && month == 12) || (day <= 20 && month == 1) -> "Козерог"
        (day >= 21 && month == 1) || (day <= 19 && month == 2) -> "Водолей"
        (day >= 20 && month == 2) || (day <= 20 && month == 3) -> "Рыбы"
        else -> "?"
    }
}

fun getZodiacImage(zodiac: String): Int {
    return when (zodiac) {
        "Дева" -> R.drawable.girl1
        "Рак" -> R.drawable.cansor
        else -> R.drawable.ic_launcher_foreground
    }
}

@Preview
@Composable
fun RegistrationPanelPreview() {
    GameBugsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RegistrationPanel(
                onRegisteredPlayer = {}
            )
        }
    }
}