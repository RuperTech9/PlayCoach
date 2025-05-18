package com.example.playcoach.data.daos

import androidx.room.*
import com.example.playcoach.data.entities.TeamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {

    @Query("SELECT * FROM teams ORDER BY name")
    fun getAllTeams(): Flow<List<TeamEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTeam(team: TeamEntity)
}