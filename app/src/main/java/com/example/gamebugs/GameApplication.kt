package com.example.gamebugs

import android.app.Application
import com.example.gamebugs.dataBase.AppDatabase
import com.example.gamebugs.model.viewModel.AppViewModelFactory
import com.example.gamebugs.dataBase.repository.PlayerRepository
import com.example.gamebugs.dataBase.repository.RecordsRepository

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

    val appViewModelFactory: AppViewModelFactory by lazy {
        AppViewModelFactory(recordsRepository, playerRepository)
    }
}