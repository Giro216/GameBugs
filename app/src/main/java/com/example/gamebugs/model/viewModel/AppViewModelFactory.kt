package com.example.gamebugs.model.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gamebugs.dataBase.repository.PlayerRepository
import com.example.gamebugs.dataBase.repository.RecordsRepository
import com.example.gamebugs.network.repository.GoldCurrencyRepository

@Suppress("UNCHECKED_CAST")
class AppViewModelFactory(
    private val recordsRepository: RecordsRepository,
    private val playerRepository: PlayerRepository,
    private val goldCurrencyRepository: GoldCurrencyRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GameViewModel::class.java) -> {
                GameViewModel(recordsRepository) as T
            }
            modelClass.isAssignableFrom(PlayerViewModel::class.java) -> {
                PlayerViewModel(playerRepository) as T
            }
            modelClass.isAssignableFrom(CurrencyViewModel::class.java) -> {
                CurrencyViewModel(goldCurrencyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}