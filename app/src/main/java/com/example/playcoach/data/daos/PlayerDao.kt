package com.example.playcoach.data.daos

import androidx.room.*
import com.example.playcoach.data.entities.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity)

    @Delete
    suspend fun deletePlayer(player: PlayerEntity)

    @Query("SELECT * FROM players WHERE team = :team ORDER BY firstName")
    fun getPlayersByTeam(team: String): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE number = :playerId LIMIT 1")
    fun getPlayerByJerseyNumber(playerId: Int): Flow<PlayerEntity?>
}