package com.example.playcoach.data.daos

import androidx.room.*
import com.example.playcoach.data.entities.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("SELECT * FROM events WHERE date = :date")
    fun getEventsByDate(date: String): Flow<List<EventEntity>>

    @Query("SELECT * FROM events ORDER BY date DESC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE team = :team ORDER BY date ASC")
    fun getEventsByTeam(team: String): Flow<List<EventEntity>>
}
