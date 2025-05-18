package com.example.playcoach.data.daos

import androidx.room.*
import com.example.playcoach.data.entities.CallUpEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CallUpDao {

    @Query("SELECT playerId FROM call_ups WHERE matchdayId = :matchdayId AND calledUp = 1")
    fun getCalledUpPlayersForMatchday(matchdayId: Int): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallUp(callUp: CallUpEntity)

    @Query("DELETE FROM call_ups WHERE matchdayId = :matchdayId")
    suspend fun deleteCallUpsForMatchday(matchdayId: Int)
}
