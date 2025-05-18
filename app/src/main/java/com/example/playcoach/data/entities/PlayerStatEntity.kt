package com.example.playcoach.data.entities

import androidx.room.Entity

@Entity(
    tableName = "player_stats",
    primaryKeys = ["playerId", "matchdayId"]
)
data class PlayerStatEntity(
    val playerId: Int,
    val matchdayId: Int,
    val minutesPlayed: Int,
    val goals: Int,
    val assists: Int,
    val yellowCards: Int,
    val redCards: Int,
    val wasStarter: Boolean
)