package com.example.playcoach.data.repositories

import com.example.playcoach.data.daos.CallUpDao
import com.example.playcoach.data.entities.CallUpEntity
import kotlinx.coroutines.flow.Flow

class CallUpRepository(private val dao: CallUpDao) {

    fun getCalledUpPlayers(matchdayId: Int): Flow<List<Int>> =
        dao.getCalledUpPlayersForMatchday(matchdayId)

    suspend fun saveCallUps(matchdayId: Int, playerIds: List<Int>) {
        dao.deleteCallUpsForMatchday(matchdayId)
        playerIds.forEach { playerId ->
            dao.insertCallUp(
                CallUpEntity(
                    matchdayId = matchdayId,
                    playerId = playerId,
                    calledUp = true
                )
            )
        }
    }
}
