package com.example.playcoach.data.repositories

import com.example.playcoach.data.daos.PlayerDao
import com.example.playcoach.data.entities.PlayerEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class PlayerRepository @Inject constructor(
    private val playerDao: PlayerDao
) {

    fun getPlayersByTeam(team: String): Flow<List<PlayerEntity>> {
        return playerDao.getPlayersByTeam(team)
    }

    suspend fun insertPlayer(player: PlayerEntity) {
        playerDao.insertPlayer(player)
    }

    suspend fun deletePlayer(player: PlayerEntity) {
        playerDao.deletePlayer(player)
    }

    fun getPlayerByJerseyNumber(id: Int): Flow<PlayerEntity?> {
        return playerDao.getPlayerByJerseyNumber(id)
    }
}
