package com.example.playcoach.data.repositories

import com.example.playcoach.data.daos.FormationDao
import com.example.playcoach.data.daos.PlayerPositionDao
import com.example.playcoach.data.entities.FormationEntity
import com.example.playcoach.data.entities.PlayerPositionEntity
import kotlinx.coroutines.flow.Flow

class FormationRepository(
    private val formationDao: FormationDao,
    private val playerPositionDao: PlayerPositionDao
) {

    suspend fun createFormationWithPositions(
        formation: FormationEntity,
        positions: List<PlayerPositionEntity>
    ) {
        val formationId = formationDao.insertFormation(formation).toInt()
        val positionsWithId = positions.map { it.copy(formationId = formationId) }
        playerPositionDao.insertPositions(positionsWithId)
    }

    fun getFormationsByTeam(team: String): Flow<List<FormationEntity>> {
        return formationDao.getFormationsByTeam(team)
    }

    fun getPositionsByFormation(formationId: Int): Flow<List<PlayerPositionEntity>> {
        return playerPositionDao.getPositions(formationId)
    }

    suspend fun updatePositions(formationId: Int, newPositions: List<PlayerPositionEntity>) {
        playerPositionDao.deletePositions(formationId)
        playerPositionDao.insertPositions(newPositions)
    }

    suspend fun getFormationById(id: Int): FormationEntity? {
        return formationDao.getFormationById(id)
    }

    suspend fun deleteFormation(formation: FormationEntity) {
        playerPositionDao.deletePositions(formation.id)
        formationDao.delete(formation)
    }
}
