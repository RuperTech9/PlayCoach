package com.example.playcoach.data.daos

import androidx.room.*
import com.example.playcoach.data.entities.FormationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FormationDao {

    @Insert
    suspend fun insertFormation(formation: FormationEntity): Long

    @Delete
    suspend fun delete(formation: FormationEntity)

    @Query("SELECT * FROM formations WHERE team = :team")
    fun getFormationsByTeam(team: String): Flow<List<FormationEntity>>

    @Query("SELECT * FROM formations WHERE id = :id")
    suspend fun getFormationById(id: Int): FormationEntity?
}
