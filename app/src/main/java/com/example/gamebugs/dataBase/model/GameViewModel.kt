package com.example.gamebugs.dataBase.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamebugs.dataBase.repository.IRecordsRepository
import com.example.gamebugs.dataBase.repository.RecordsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel(private val repository: IRecordsRepository) : ViewModel() {

//    private val _gameScore = MutableStateFlow(0)
//    val gameScore: StateFlow<Int> = _gameScore
//
//    private val _gameState = MutableStateFlow("playing")
//    val gameState: StateFlow<String> = _gameState
//
//    fun updateScore(points: Int) {
//        _gameScore.value += points
//    }
//
//    fun setGameState(state: String) {
//        _gameState.value = state
//    }

    private val _records = MutableStateFlow<List<GameRecord>>(emptyList())
    val records: StateFlow<List<GameRecord>> = _records

    fun loadRecords() {
        viewModelScope.launch {
            _records.value = repository.getTopRecords(10)
        }
    }

    fun saveRecord(record: GameRecord) {
        viewModelScope.launch {
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

}

class GameViewModelFactory(
    private val repository: RecordsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}