package com.example.playcoach.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matchdays")
data class MatchdayEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val matchdayNumber: Int,
    val time: String,
    val date: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeGoals: Int,
    val awayGoals: Int,
    val summary: String = "",
    val team: String,
    val played: Boolean = false
)