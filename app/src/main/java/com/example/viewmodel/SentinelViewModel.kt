package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.EmergencyLog
import com.example.data.EmergencyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SentinelViewModel(private val repository: EmergencyRepository) : ViewModel() {

    val uiState: StateFlow<List<EmergencyLog>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun logEmergency(type: String, severity: String, latitude: Double, longitude: Double, status: String) {
        viewModelScope.launch {
            repository.insertLog(
                EmergencyLog(
                    type = type,
                    severity = severity,
                    latitude = latitude,
                    longitude = longitude,
                    status = status
                )
            )
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }
}

class SentinelViewModelFactory(private val repository: EmergencyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SentinelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SentinelViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
