package com.example.playcoach.data.repositories

import com.example.playcoach.data.daos.TeamDao
import com.example.playcoach.data.entities.TeamEntity
import kotlinx.coroutines.flow.Flow

class TeamRepository(private val teamDao: TeamDao) {

    fun getAllTeams(): Flow<List<TeamEntity>> {
        return teamDao.getAllTeams()
    }

    suspend fun insertTeam(team: TeamEntity) {
        teamDao.insertTeam(team)
    }
}