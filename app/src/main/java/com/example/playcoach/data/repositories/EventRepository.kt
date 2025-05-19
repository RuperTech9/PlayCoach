package com.example.playcoach.data.repositories

import com.example.playcoach.data.daos.AbsenceDao
import com.example.playcoach.data.daos.EventDao
import com.example.playcoach.data.entities.EventEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class EventRepository @Inject constructor(
    private val eventDao: EventDao,
    private val absenceDao: AbsenceDao
) {

    fun getEventsByDate(date: String): Flow<List<EventEntity>> {
        return eventDao.getEventsByDate(date)
    }

    fun getAllEvents(): Flow<List<EventEntity>> {
        return eventDao.getAllEvents()
    }

    suspend fun insertEvent(event: EventEntity) {
        eventDao.insertEvent(event)
    }

    suspend fun deleteEventAndAbsences(event: EventEntity) {
        eventDao.deleteEvent(event)
        absenceDao.deleteAttendanceByDate(event.date)
    }

    suspend fun updateEvent(event: EventEntity) {
        eventDao.updateEvent(event)
    }

    fun getEventsByTeam(team: String): Flow<List<EventEntity>> {
        return eventDao.getEventsByTeam(team)
    }
}
