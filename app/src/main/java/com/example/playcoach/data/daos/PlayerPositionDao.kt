package com.example.playcoach.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.playcoach.data.entities.PlayerPositionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerPositionDao {

    @Insert
    suspend fun insertPositions(positions: List<PlayerPositionEntity>)

    @Query("SELECT * FROM formation_positions WHERE formationId = :formationId")
    fun getPositions(formationId: Int): Flow<List<PlayerPositionEntity>>

    @Query("DELETE FROM formation_positions WHERE formationId = :formationId")
    suspend fun deletePositions(formationId: Int)
}