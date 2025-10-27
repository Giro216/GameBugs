package com.example.gamebugs.model.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gamebugs.dataBase.repository.PlayerRepository
import com.example.gamebugs.dataBase.repository.RecordsRepository

@Suppress("UNCHECKED_CAST")
class AppViewModelFactory(
    private val recordsRepository: RecordsRepository,
    private val playerRepository: PlayerRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GameViewModel::class.java) -> {
                GameViewModel(recordsRepository) as T
            }
            modelClass.isAssignableFrom(PlayerViewModel::class.java) -> {
                PlayerViewModel(playerRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}