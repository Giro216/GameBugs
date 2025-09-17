package com.example.gamebugs.ui.components

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import androidx.compose.ui.res.stringResource
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
fun RegistrationPanel() {
    // Состояния для хранения введённых данных
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf(1f) }
    var expanded by remember { mutableStateOf(false) }
    var birthDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var player by remember { mutableStateOf<Player?>(null) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 10.dp)
        )

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Введите ФИО") },
            modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.Start)
        )

        Text(
            "Пол:",
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
                    Text(
                        text = option,
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Курс (Dropdown)
        Box {
            var textBlock by remember { mutableStateOf("course") }
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
            RegistrationPanel()
        }
    }
}