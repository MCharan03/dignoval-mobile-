package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_logs")
data class EmergencyLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val severity: String,
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double,
    val longitude: Double,
    val status: String // "Sent", "Cancelled", "Received"
)
