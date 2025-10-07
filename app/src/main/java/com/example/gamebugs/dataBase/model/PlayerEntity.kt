package com.example.gamebugs.dataBase.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val gender: String,
    val course: String,
    val difficulty: Int,
    val birthDate: Long,
    val zodiac: String
)
