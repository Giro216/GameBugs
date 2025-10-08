package com.example.gamebugs.dataBase.repository

import com.example.gamebugs.dataBase.dao.PlayerDao
import com.example.gamebugs.dataBase.model.PlayerEntity

interface IPlayerRepository {
    suspend fun savePlayer(player: PlayerEntity)
    suspend fun getAllPlayers(): List<PlayerEntity>
    suspend fun deletePlayer(player: PlayerEntity)
    suspend fun getPlayerByName(name: String): PlayerEntity?

    suspend fun isPlayerExistByName(name: String) : Boolean
}

class PlayerRepository(private val playerDao: PlayerDao) : IPlayerRepository {
    override suspend fun savePlayer(player: PlayerEntity) {
        if (!playerDao.isPlayerExists(player.name)) {
            playerDao.insertPlayer(player)
        }
    }

    override suspend fun getAllPlayers(): List<PlayerEntity> {
        println("Getting all players")
        return playerDao.getAllPlayers().also {
            println("Retrieved ${it.size} players")
        }
    }

    override suspend fun deletePlayer(player: PlayerEntity) {
        return playerDao.deletePlayer(player)
    }

    override suspend fun getPlayerByName(name: String): PlayerEntity? {
        return playerDao.getPlayerByName(name)
    }

    override suspend fun isPlayerExistByName(name: String): Boolean {
        return playerDao.isPlayerExists(name)
    }


}

class MockPlayerRepository : IPlayerRepository {
    val mockPlayers = mutableListOf(
        PlayerEntity(
            name = "Макс",
            gender = "муж",
            course = "4 курс",
            difficulty = 3,
            birthDate = System.currentTimeMillis() - (25 * 365 * 24 * 60 * 60 * 1000L),
            zodiac = "Рак"
        ),
        PlayerEntity(
            name = "Анна",
            gender = "жен",
            course = "2 курс",
            difficulty = 2,
            birthDate = System.currentTimeMillis() - (20 * 365 * 24 * 60 * 60 * 1000L),
            zodiac = "Дева"
        )
    )

    override suspend fun savePlayer(player: PlayerEntity) {
        mockPlayers.removeAll { it.name == player.name }
        mockPlayers.add(player)
    }

    override suspend fun getAllPlayers(): List<PlayerEntity> {
        return mockPlayers.toList()
    }

    override suspend fun deletePlayer(player: PlayerEntity) {
        mockPlayers.removeAll { it.name == player.name }
    }

    override suspend fun getPlayerByName(name: String): PlayerEntity? {
        return mockPlayers.find { it.name == name }
    }

    override suspend fun isPlayerExistByName(name: String): Boolean {
        return true
    }
}