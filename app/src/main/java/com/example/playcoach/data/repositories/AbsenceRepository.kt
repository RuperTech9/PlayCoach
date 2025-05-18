package com.example.playcoach.data.repositories

import com.example.playcoach.data.daos.AbsenceDao
import com.example.playcoach.data.entities.AbsenceEntity
import kotlinx.coroutines.flow.Flow

class AbsenceRepository(private val absenceDao: AbsenceDao) {

    fun getAttendanceByDate(date: String): Flow<List<AbsenceEntity>> {
        return absenceDao.getAttendanceByDate(date)
    }

    fun getAttendanceByDateAndPlayer(date: String, playerId: Int): Flow<AbsenceEntity?> {
        return absenceDao.getAttendanceByDateAndPlayer(date, playerId)
    }

    suspend fun insertAttendance(attendance: AbsenceEntity) {
        absenceDao.insertAttendance(attendance)
    }

    suspend fun updateAttendance(attendance: AbsenceEntity) {
        absenceDao.updateAttendance(attendance)
    }

    suspend fun deleteAttendanceByDate(date: String) {
        absenceDao.deleteAttendanceByDate(date)
    }

    suspend fun deleteOrphanedAttendance() {
        absenceDao.deleteOrphanedAttendance()
    }

    fun countAbsences(playerId: Int): Flow<Int> {
        return absenceDao.countAbsences(playerId)
    }

    suspend fun registerAttendanceForDate(
        date: String,
        playerIds: List<Int>,
        presentIds: List<Int>
    ) {
        playerIds.forEach { playerId ->
            val isPresent = playerId in presentIds
            val current = absenceDao.getAttendanceByDateAndPlayerDirect(date, playerId)
            if (current == null) {
                absenceDao.insertAttendance(
                    AbsenceEntity(date = date, playerId = playerId, present = isPresent)
                )
            } else {
                absenceDao.updateAttendance(current.copy(present = isPresent))
            }
        }
    }
}
