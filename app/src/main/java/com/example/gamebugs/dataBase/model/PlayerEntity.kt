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

//val PlayerSaver = Saver<PlayerEntity?, List<Any>>(
//    save = { playerEntity ->
//        listOf(
//            playerEntity?.name ?: ,
//            playerEntity.id,
//            playerEntity.course,
//            playerEntity.gender,
//            playerEntity.birthDate,
//            playerEntity.difficulty,
//            playerEntity.zodiac
//        )
//    },
//    restore = { restored ->
//        PlayerEntity(
//            name = restored[0] as String,
//            id = restored[1] as Long,
//            course = restored[2] as String,
//            gender = restored[3] as String,
//            birthDate = restored[4] as Long,
//            difficulty = restored[5] as Int,
//            zodiac = restored[6] as String,
//        )
//    }
//)
