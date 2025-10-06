package com.example.gamebugs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.gamebugs.dataBase.AppDatabase
import com.example.gamebugs.dataBase.model.GameViewModel
import com.example.gamebugs.dataBase.model.GameViewModelFactory
import com.example.gamebugs.dataBase.repository.RecordsRepository
import com.example.gamebugs.ui.config.AppNavigation
import com.example.gamebugs.ui.theme.GameBugsTheme

class MainActivity : ComponentActivity() {
    private val gameViewModel: GameViewModel by viewModels {
        GameViewModelFactory(createRecordsRepository())
    }

    private fun createRecordsRepository(): RecordsRepository {
        val database = AppDatabase.getDatabase(this)
        return RecordsRepository(database.recordDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameBugsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    AppNavigation(gameViewModel = gameViewModel)
                }
            }
        }
    }
}