package com.example.gamebugs.ui.components

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gamebugs.R
import com.example.gamebugs.ui.theme.GameBugsTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class Player(
    val name: String,
    val gender: String,
    val course: String,
    val difficulty: Int,
    val birthDate: Long,
    val zodiac: String
)

@SuppressLint("SimpleDateFormat")
@Composable
fun RegistrationPanel(
    onRegisteredPlayer: (Player) -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf(1f) }
    var expanded by remember { mutableStateOf(false) }
    var birthDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var isRegistered by remember { mutableStateOf(false) }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Введите ФИО") },
            modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.Start)
                .padding(vertical = 5.dp)
        )

        Text("Пол:",
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyLarge
        )

        Row {
            listOf("Муж", "Жен").forEach { option ->
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = (gender == option),
                            onClick = { gender = option }
                        )
                        .padding(end = 8.dp),
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

        Box {
            var textBlock by remember { mutableStateOf("Выберите курс") }
            OutlinedButton(onClick = { expanded = true }) {
                Text(textBlock)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf("1 курс", "2 курс", "3 курс", "4 курс").forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            course = it
                            textBlock = it
                            expanded = false
                        }
                    )
                }
            }
        }

        Text("Сложность: ${difficulty.toInt()}")
        Slider(
            value = difficulty,
            onValueChange = { difficulty = it },
            steps = 3,
            valueRange = 1f..5f,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedButton(onClick = { datePickerDialog.show() }) {
            Text(text = "Выбрать дату рождения")
        }
        Text("Дата: ${SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(birthDate))}")

        Button(
            onClick = {
                if (fullName.isNotBlank() && gender.isNotBlank() && course.isNotBlank()) {
                    val zodiac = getZodiac(birthDate)
                    val player = Player(
                        fullName,
                        gender,
                        course,
                        difficulty.toInt(),
                        birthDate,
                        zodiac
                    )
                    isRegistered = true
                    onRegisteredPlayer(player)
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 5.dp),
            enabled = fullName.isNotBlank() && gender.isNotBlank() && course.isNotBlank()
        ) {
            Text("Зарегистрироваться")
        }

        // Результат
        if (isRegistered) {
            Text(
                """
                Имя: $fullName
                Пол: $gender
                Курс: $course
                Сложность: ${difficulty.toInt()}
                Дата рождения: ${SimpleDateFormat("dd.MM.yyyy").format(Date(birthDate))}
                Знак зодиака: ${getZodiac(birthDate)}
            """.trimIndent()
            )

            Image(
                painterResource(id = getZodiacImage(getZodiac(birthDate))),
                contentDescription = null
            )

            Spacer(Modifier.height(12.dp))
        }
    }
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