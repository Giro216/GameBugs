package com.example.gamebugs.dataBase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gamebugs.dataBase.model.GameRecord

@Dao
interface RecordsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(newRecord: GameRecord)

    @Query("DELETE FROM GameRecords")
    suspend fun resetAllRecords()

    @Query("SELECT * FROM GameRecords ORDER BY score DESC LIMIT :limit")
    suspend fun getTopRecords(limit: Int): List<GameRecord>

    @Query("SELECT * FROM GameRecords WHERE playerId = :playerId")
    suspend fun getRecordByPlayerId(playerId: Long): GameRecord?

    @Query("DELETE FROM GameRecords WHERE playerId = :playerId")
    suspend fun deleteRecordByPlayerId(playerId: Long)
}