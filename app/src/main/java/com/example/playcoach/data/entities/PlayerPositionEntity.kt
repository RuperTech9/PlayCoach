package com.example.playcoach.data.entities

import androidx.room.Entity

@Entity(tableName = "formation_positions", primaryKeys = ["formationId", "playerId"])
data class PlayerPositionEntity(
    val formationId: Int,
    val playerId: Int,
    val positionX: Float,
    val positionY: Float
)