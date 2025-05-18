package com.example.playcoach.data.entities

import androidx.room.Entity

@Entity(tableName = "call_ups", primaryKeys = ["matchdayId", "playerId"])
data class CallUpEntity(
    val matchdayId: Int,
    val playerId: Int,
    val calledUp: Boolean
)