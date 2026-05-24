package com.example.data

import kotlinx.coroutines.flow.Flow

class EmergencyRepository(private val emergencyLogDao: EmergencyLogDao) {
    val allLogs: Flow<List<EmergencyLog>> = emergencyLogDao.getAllLogs()

    suspend fun insertLog(log: EmergencyLog) {
        emergencyLogDao.insertLog(log)
    }

    suspend fun clearLogs() {
        emergencyLogDao.clearLogs()
    }
}
