package com.example.gamebugs.dataBase.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GameRecords")
data class GameRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val playerName: String,

    val score: Int,

    val difficulty: Int,

    val date: Long = System.currentTimeMillis(),

//    val settings: Settings
)