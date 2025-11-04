package com.example.gamebugs.dataBase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.gamebugs.dataBase.model.PlayerEntity

@Dao
interface PlayerDao {
    @Insert
    suspend fun insertPlayer(player: PlayerEntity)

    @Query("SELECT * FROM players ORDER BY name")
    suspend fun getAllPlayers(): List<PlayerEntity>

    @Delete
    suspend fun deletePlayer(player: PlayerEntity)

    @Query("SELECT * FROM players WHERE name = :name")
    suspend fun getPlayerByName(name: String): PlayerEntity?

    @Query("SELECT COUNT(*) FROM players WHERE name = :name")
    suspend fun isPlayerExists(name: String): Boolean

    @Query("SELECT * FROM players WHERE id = :id")
    suspend fun getPlayerById(id: Long): PlayerEntity?
}