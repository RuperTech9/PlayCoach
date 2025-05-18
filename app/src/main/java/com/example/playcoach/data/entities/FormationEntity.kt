package com.example.playcoach.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "formations")
data class FormationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val team: String
)