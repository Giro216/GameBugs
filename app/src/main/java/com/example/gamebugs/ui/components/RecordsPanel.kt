package com.example.gamebugs.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gamebugs.dataBase.model.GameViewModel
import com.example.gamebugs.dataBase.repository.MockRecordsRepository
import com.example.gamebugs.ui.theme.GameBugsTheme

@Composable
fun RecordsPanel(gameViewModel: GameViewModel) {
    val records by gameViewModel.records.collectAsState()

    LaunchedEffect(Unit) {
        gameViewModel.loadRecords()
    }

    Column {
        if (records.isEmpty()) {
            Text("Рекордов пока нет")
        } else {
            records.forEach { record ->
                Text("${record.playerName} = ${record.score}")
            }
        }

        Button(
            onClick = {gameViewModel.resetRecords()}
        ) {
            Text("Сброс")
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
fun PreviewRecordsPanel(){
    GameBugsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val gameViewModel = GameViewModel(
                repository = MockRecordsRepository()
            )

            RecordsPanel(gameViewModel)
        }
    }
}