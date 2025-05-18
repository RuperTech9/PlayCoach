package com.example.playcoach.data.daos

import androidx.room.*
import com.example.playcoach.data.entities.PlayerStatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerStatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStat(stat: PlayerStatEntity)

    @Query("SELECT * FROM player_stats WHERE playerId = :playerId")
    fun getStatsByPlayer(playerId: Int): Flow<List<PlayerStatEntity>>

    @Query("SELECT * FROM player_stats WHERE matchdayId = :matchdayId")
    fun getStatsByMatchday(matchdayId: Int): Flow<List<PlayerStatEntity>>

    @Query("SELECT * FROM player_stats")
    fun getAllStats(): Flow<List<PlayerStatEntity>>

    @Query("SELECT * FROM player_stats WHERE playerId = :playerId AND matchdayId = :matchdayId LIMIT 1")
    fun getStatForPlayerInMatchday(playerId: Int, matchdayId: Int): Flow<PlayerStatEntity?>
}
