package com.example.playcoach.data.repositories

import com.example.playcoach.data.daos.MatchdayDao
import com.example.playcoach.data.entities.MatchdayEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class MatchdayRepository @Inject constructor(
    private val matchdayDao: MatchdayDao
) {
    fun getAllMatchdays(): Flow<List<MatchdayEntity>> {
        return matchdayDao.getAllMatchdays()
    }

    fun getMatchdayById(id: Int): Flow<MatchdayEntity?> {
        return matchdayDao.getMatchdayById(id)
    }

    suspend fun insertMatchday(matchday: MatchdayEntity): Long {
        return matchdayDao.insertMatchday(matchday)
    }

    suspend fun updateMatchday(matchday: MatchdayEntity) {
        matchdayDao.updateMatchday(matchday)
    }

    fun getMatchdaysByTeam(team: String): Flow<List<MatchdayEntity>> {
        return matchdayDao.getMatchdaysByTeam(team)
    }

    suspend fun insertMatchdaysIfNotExist(team: String, matchdays: List<MatchdayEntity>) {
        val existing = matchdayDao.getMatchdaysByTeamOnce(team)
        if (existing.isEmpty()) {
            matchdayDao.insertMultiple(matchdays)
        }
    }
}
