package com.example.gamebugs

import android.app.Application
import com.example.gamebugs.dataBase.AppDatabase
import com.example.gamebugs.dataBase.repository.PlayerRepository
import com.example.gamebugs.dataBase.repository.RecordsRepository
import com.example.gamebugs.model.viewModel.AppViewModelFactory
import com.example.gamebugs.network.repository.GoldCurrencyRepository
import com.example.gamebugs.network.service.CbApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

class GameApplication : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    val recordsRepository: RecordsRepository by lazy {
        RecordsRepository(database.recordDao())
    }

    val playerRepository: PlayerRepository by lazy {
        PlayerRepository(database.playerDao())
    }

    private val cbrApi: CbApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val serializer = org.simpleframework.xml.core.Persister()

        Retrofit.Builder()
            .baseUrl("https://www.cbr.ru/")
            .client(client)
            .addConverterFactory(SimpleXmlConverterFactory.create(serializer))
            .build()
            .create(CbApiService::class.java)
    }
    val goldCurrencyRepository: GoldCurrencyRepository by lazy {
        GoldCurrencyRepository(cbrApi)
    }

    val appViewModelFactory: AppViewModelFactory by lazy {
        AppViewModelFactory(recordsRepository, playerRepository, goldCurrencyRepository)
    }
}