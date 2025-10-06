package com.example.gamebugs.dataBase.repository

import com.example.gamebugs.dataBase.dao.RecordsDao
import com.example.gamebugs.dataBase.model.GameRecord

interface IRecordsRepository {
    suspend fun saveRecord(record: GameRecord)
    suspend fun getTopRecords(limit: Int = 10): List<GameRecord>
}

class RecordsRepository(private val recordDao: RecordsDao) : IRecordsRepository {

    override suspend fun saveRecord(record: GameRecord) {
        recordDao.insertRecord(record)
    }

    override suspend fun getTopRecords(limit: Int): List<GameRecord> {
        return recordDao.getTopRecords(limit)
    }

    suspend fun getPlayerRecords(playerName: String): List<GameRecord> {
        return recordDao.getPlayerRecords(playerName)
    }
}

class MockRecordsRepository : IRecordsRepository {
    override suspend fun saveRecord(record: GameRecord) { }
    override suspend fun getTopRecords(limit: Int): List<GameRecord> = listOf(
        GameRecord(playerName = "Preview1", score = 150, difficulty = 2),
        GameRecord(playerName = "Preview2", score = 120, difficulty = 3)
    )
}