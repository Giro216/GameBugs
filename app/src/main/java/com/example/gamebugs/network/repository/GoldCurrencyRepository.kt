package com.example.gamebugs.network.repository

import android.annotation.SuppressLint
import com.example.gamebugs.network.service.CbApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date

interface IMetalCurrencyRepository{
    val price: StateFlow<Double>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>

    suspend fun loadPrice()
    fun getReward(): Int
    fun getFormattedPrice(): String
}

class GoldCurrencyRepository (
    private val cbApiService: CbApiService
) : IMetalCurrencyRepository {
    private val _price = MutableStateFlow(0.0)
    override val price: StateFlow<Double> = _price

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error

    @SuppressLint("SimpleDateFormat")
    override suspend fun loadPrice(){
        _isLoading.value = true
        _error.value = null

        try {
            val currDate = SimpleDateFormat("dd/mm/yyyy").format(Date())

            val response = cbApiService.getGoldPrices(currDate, currDate)

            val goldRecord = response.records.find{it.isGold()}

            goldRecord?.let { price ->
                println("Курс золота: покупка=${price.buy}, продажа=${price.sell}")
                _price.value = price.toDouble()
            } ?: run {
                val errorMsg = "Запись о золоте не найдена в ответе API"
                println(errorMsg)
                _error.value = errorMsg

                _price.value = 7500.0
            }
        } catch (e: Exception) {
            val errorMsg = "Ошибка загрузки: ${e.message}"
            println(errorMsg)
            _error.value = errorMsg
            _price.value = 7500.0
        } finally {
            _isLoading.value = false
        }

    }

    override fun getReward(): Int {
        val reward = (_price.value / 100).toInt()
        println("Рассчитана награда: $reward очков (курс: ${_price.value})")
        return maxOf(reward, 10)
    }

    @SuppressLint("DefaultLocale")
    override fun getFormattedPrice(): String {
        return String.format("%.3f руб/г", _price.value)
    }
}

class MockMetalCurrencyRepository(
) : IMetalCurrencyRepository{
    override val price: StateFlow<Double> = MutableStateFlow(7500.0)
    override val isLoading: StateFlow<Boolean> = MutableStateFlow(false)
    override val error: StateFlow<String?> = MutableStateFlow("err")
    override suspend fun loadPrice() {}

    override fun getReward(): Int {
        return 7500
    }

    override fun getFormattedPrice(): String {
        return String.format("${7500}.3f руб/г")
    }

}