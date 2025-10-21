package com.example.gamebugs.dataBase.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "GameRecords",
    foreignKeys = [ForeignKey(
        entity = PlayerEntity::class,
        parentColumns = ["id"],
        childColumns = ["playerId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["playerId"], unique = true)]
)
data class GameRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val playerId: Long,

    val playerName: String,

    val score: Int,

    val difficulty: Int,

    val date: Long = System.currentTimeMillis()
)