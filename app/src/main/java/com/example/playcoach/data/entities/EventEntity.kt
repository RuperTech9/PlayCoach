package com.example.playcoach.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val type: String,
    val time: String,
    val team: String
)