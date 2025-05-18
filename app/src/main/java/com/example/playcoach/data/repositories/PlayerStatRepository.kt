package com.example.playcoach.data.repositories

import com.example.playcoach.data.daos.PlayerStatDao
import com.example.playcoach.data.entities.PlayerStatEntity
import kotlinx.coroutines.flow.Flow

class PlayerStatRepository(private val playerStatDao: PlayerStatDao) {

    fun getStatsByPlayer(playerId: Int): Flow<List<PlayerStatEntity>> {
        return playerStatDao.getStatsByPlayer(playerId)
    }

    fun getStatsByMatchday(matchdayId: Int): Flow<List<PlayerStatEntity>> {
        return playerStatDao.getStatsByMatchday(matchdayId)
    }

    fun getAllStats(): Flow<List<PlayerStatEntity>> {
        return playerStatDao.getAllStats()
    }

    suspend fun insertStat(stat: PlayerStatEntity) {
        playerStatDao.insertStat(stat)
    }

    fun getStatByPlayerAndMatchday(playerId: Int, matchdayId: Int): Flow<PlayerStatEntity?> {
        return playerStatDao.getStatForPlayerInMatchday(playerId, matchdayId)
    }
}
