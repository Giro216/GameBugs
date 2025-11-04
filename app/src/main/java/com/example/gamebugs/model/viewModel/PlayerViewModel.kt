package com.example.gamebugs.model.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamebugs.dataBase.model.PlayerEntity
import com.example.gamebugs.dataBase.repository.IPlayerRepository
import com.example.gamebugs.model.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PlayerViewModel(private val repository: IPlayerRepository) : ViewModel() {

    private val _players = MutableStateFlow<List<PlayerEntity>>(emptyList())
    val players: StateFlow<List<PlayerEntity>> = _players

    var playerEntity: PlayerEntity? = null
    var settings = Settings()

    fun loadPlayers() {
        viewModelScope.launch {
            _players.value = repository.getAllPlayers()
        }
    }

    fun savePlayer(player: PlayerEntity) {
        viewModelScope.launch {
            repository.savePlayer(player)
            loadPlayers()
        }
    }

    fun deletePlayer(player: PlayerEntity) {
        viewModelScope.launch {
            repository.deletePlayer(player)
            loadPlayers()
        }
    }

    fun isPlayerExistByName(name: String): Boolean{
        var result = true
        viewModelScope.launch {
            result = repository.isPlayerExistByName(name)
        }
        return result
    }

    // PlayerViewModel.kt
    fun getPlayerById(playerId: Long): PlayerEntity? {
        return try {
            runBlocking {
                repository.getPlayerById(playerId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}