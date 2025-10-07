package com.example.gamebugs.dataBase.model.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamebugs.dataBase.model.PlayerEntity
import com.example.gamebugs.dataBase.repository.IPlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(private val repository: IPlayerRepository) : ViewModel() {

    private val _players = MutableStateFlow<List<PlayerEntity>>(emptyList())
    val players: StateFlow<List<PlayerEntity>> = _players

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
}