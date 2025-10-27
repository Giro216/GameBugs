package com.example.gamebugs.model.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamebugs.dataBase.model.GameRecord
import com.example.gamebugs.dataBase.repository.IRecordsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel(private val repository: IRecordsRepository) : ViewModel() {

    private val _records = MutableStateFlow<List<GameRecord>>(emptyList())
    val records: StateFlow<List<GameRecord>> = _records

    fun loadRecords() {
        viewModelScope.launch {
            _records.value = repository.getTopRecords(10)
        }
    }

    fun saveRecord(playerId: Long, playerName: String, score: Int, difficulty: Int) {
        viewModelScope.launch {
            val record = GameRecord(
                playerId = playerId,
                score = score,
                difficulty = difficulty,
                playerName = playerName
            )
            repository.saveRecord(record)
            loadRecords()
        }
    }

    fun resetRecords(){
        viewModelScope.launch {
            repository.resetAllRecords()
            loadRecords()
        }
    }

    suspend fun getPlayerRecord(playerId: Long): GameRecord? {
        return repository.getRecordByPlayerId(playerId)
    }
}