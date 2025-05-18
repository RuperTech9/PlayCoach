package com.example.playcoach.data.daos

import androidx.room.*
import com.example.playcoach.data.entities.CoachEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoachDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoach(coach: CoachEntity)

    @Delete
    suspend fun deleteCoach(coach: CoachEntity)

    @Query("SELECT * FROM coaches WHERE team = :team ORDER BY name")
    fun getCoachesByTeam(team: String): Flow<List<CoachEntity>>
}
