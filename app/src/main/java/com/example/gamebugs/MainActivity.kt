package com.example.gamebugs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.gamebugs.model.viewModel.GameViewModel
import com.example.gamebugs.model.viewModel.PlayerViewModel
import com.example.gamebugs.ui.config.AppNavigation
import com.example.gamebugs.ui.theme.GameBugsTheme

class MainActivity : ComponentActivity() {
    private val gameViewModel: GameViewModel by viewModels {
        (application as GameApplication).appViewModelFactory
    }

    private val playerViewModel: PlayerViewModel by viewModels {
        (application as GameApplication).appViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GameBugsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    AppNavigation(
                        gameViewModel = gameViewModel,
                        playerViewModel = playerViewModel
                    )
                }
            }
        }
    }
}