package com.example.gamebugs.dataBase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gamebugs.dataBase.model.GameRecord

@Dao
interface RecordsDao {

    @Insert
    suspend fun insertRecord(newRecord: GameRecord)

    @Query("DELETE FROM GameRecords")
    suspend fun resetAllRecords()

    @Query("SELECT * FROM GameRecords ORDER BY score DESC LIMIT :limit")
    suspend fun getTopRecords(limit: Int): List<GameRecord>

    @Query("SELECT * FROM GameRecords WHERE playerName = :playerName ORDER BY score DESC")
    suspend fun getPlayerRecords(playerName: String): List<GameRecord>
}