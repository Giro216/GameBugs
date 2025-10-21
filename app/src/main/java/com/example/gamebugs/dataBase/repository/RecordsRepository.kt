package com.example.gamebugs.dataBase.repository

import com.example.gamebugs.dataBase.dao.RecordsDao
import com.example.gamebugs.dataBase.model.GameRecord

interface IRecordsRepository {
    suspend fun saveRecord(record: GameRecord)
    suspend fun getTopRecords(limit: Int = 10): List<GameRecord>
    suspend fun getRecordByPlayerId(playerId: Long): GameRecord?
    suspend fun resetAllRecords()
}

class RecordsRepository(private val recordDao: RecordsDao) : IRecordsRepository {

    override suspend fun saveRecord(record: GameRecord) {
        recordDao.insertRecord(record)
    }

    override suspend fun getTopRecords(limit: Int): List<GameRecord> {
        return recordDao.getTopRecords(limit)
    }

    override suspend fun getRecordByPlayerId(playerId: Long): GameRecord? {
        return recordDao.getRecordByPlayerId(playerId)
    }

    override suspend fun resetAllRecords() {
        recordDao.resetAllRecords()
    }

    suspend fun deleteRecordByPlayerId(playerId: Long) {
        recordDao.deleteRecordByPlayerId(playerId)
    }
}

class MockRecordsRepository : IRecordsRepository {
    override suspend fun saveRecord(record: GameRecord) { }
    override suspend fun getTopRecords(limit: Int): List<GameRecord> = listOf(
        GameRecord(playerId = 0, score = 150, difficulty = 2, playerName = "Bob"),
        GameRecord(playerId = 1, score = 120, difficulty = 3, playerName = "John")
    )

    override suspend fun getRecordByPlayerId(playerId: Long): GameRecord? {
        val mock_records = listOf(
            GameRecord(playerId = 0, score = 150, difficulty = 2, playerName = "Bob"),
            GameRecord(playerId = 1, score = 120, difficulty = 3, playerName = "John")
        )
        return mock_records.find { it.playerId == playerId }
    }

    override suspend fun resetAllRecords() { }
}