package com.example.playcoach.data.daos

import androidx.room.*
import com.example.playcoach.data.entities.MatchdayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchdayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchday(matchday: MatchdayEntity): Long

    @Update
    suspend fun updateMatchday(matchday: MatchdayEntity)

    @Query("SELECT * FROM matchdays ORDER BY date ASC")
    fun getAllMatchdays(): Flow<List<MatchdayEntity>>

    @Query("SELECT * FROM matchdays WHERE id = :matchdayId LIMIT 1")
    fun getMatchdayById(matchdayId: Int): Flow<MatchdayEntity?>

    @Query("SELECT * FROM matchdays WHERE team = :team ORDER BY date ASC")
    fun getMatchdaysByTeam(team: String): Flow<List<MatchdayEntity>>

    @Query("SELECT * FROM matchdays WHERE team = :team")
    suspend fun getMatchdaysByTeamOnce(team: String): List<MatchdayEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMultiple(matchdays: List<MatchdayEntity>)
}
