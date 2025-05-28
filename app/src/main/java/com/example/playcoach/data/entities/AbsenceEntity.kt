package com.example.playcoach.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "absences")
data class AbsenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,      // Format "yyyy-MM-dd"
    val playerId: Int,
    val present: Boolean
)
