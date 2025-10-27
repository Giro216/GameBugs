package com.example.gamebugs.model.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamebugs.network.repository.IMetalCurrencyRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CurrencyViewModel(private val repository: IMetalCurrencyRepository) : ViewModel() {

    val goldPrice: StateFlow<Double> get() = repository.price
    val isLoading: StateFlow<Boolean> get() = repository.isLoading
    val error: StateFlow<String?> get() = repository.error

    fun loadGoldPrice() {
        viewModelScope.launch {
            repository.loadPrice()
        }
    }

    fun getGoldReward(): Int {
        return repository.getReward()
    }
}