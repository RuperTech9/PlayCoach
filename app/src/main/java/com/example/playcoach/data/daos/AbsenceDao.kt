package com.example.playcoach.data.daos

import androidx.room.*
import com.example.playcoach.data.entities.AbsenceEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface AbsenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AbsenceEntity)

    @Update
    suspend fun updateAttendance(attendance: AbsenceEntity)

    @Delete
    suspend fun deleteAttendance(attendance: AbsenceEntity)

    @Query("SELECT * FROM absences WHERE date = :date")
    fun getAttendanceByDate(date: String): Flow<List<AbsenceEntity>>

    @Query("SELECT * FROM absences WHERE date = :date AND playerId = :playerId LIMIT 1")
    fun getAttendanceByDateAndPlayer(date: String, playerId: Int): Flow<AbsenceEntity?>

    @Query("SELECT COUNT(*) FROM absences WHERE playerId = :playerId AND present = 0")
    fun countAbsences(playerId: Int): Flow<Int>

    @Query("SELECT * FROM absences WHERE date = :date AND playerId = :playerId LIMIT 1")
    suspend fun getAttendanceByDateAndPlayerDirect(date: String, playerId: Int): AbsenceEntity?

    @Query("DELETE FROM absences WHERE date = :date")
    suspend fun deleteAttendanceByDate(date: String)

    @Query("DELETE FROM absences WHERE date NOT IN (SELECT date FROM events)")
    suspend fun deleteOrphanedAttendance()
}
