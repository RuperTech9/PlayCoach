package com.example.playcoach.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val number: Int,
    val firstName: String,
    val lastName: String,
    val nickname: String,
    val position: String, // "Goalkeeper" or "Player"
    val team: String
)