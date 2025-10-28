package com.example.gamebugs.network.repository

import kotlinx.coroutines.flow.StateFlow

interface IMetalCurrencyRepository{
    val price: StateFlow<Double>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>

    suspend fun loadPrice()
    fun getReward(): Int
    fun getFormattedPrice(): String
}