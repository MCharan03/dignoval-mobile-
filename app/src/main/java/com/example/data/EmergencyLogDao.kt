package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyLogDao {
    @Query("SELECT * FROM emergency_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<EmergencyLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: EmergencyLog)

    @Query("DELETE FROM emergency_logs")
    suspend fun clearLogs()
}
