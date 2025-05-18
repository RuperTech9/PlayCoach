package com.example.playcoach.data.repositories

import com.example.playcoach.data.daos.CoachDao
import com.example.playcoach.data.entities.CoachEntity
import kotlinx.coroutines.flow.Flow

class CoachRepository(private val coachDao: CoachDao) {

    fun getCoachesByTeam(team: String): Flow<List<CoachEntity>> {
        return coachDao.getCoachesByTeam(team)
    }

    suspend fun insertCoach(coach: CoachEntity) {
        coachDao.insertCoach(coach)
    }

    suspend fun deleteCoach(coach: CoachEntity) {
        coachDao.deleteCoach(coach)
    }
}
