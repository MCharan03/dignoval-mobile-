package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ble.BleMeshService
import com.example.data.AppDatabase
import com.example.data.EmergencyRepository
import com.example.sensors.SensorDetectionManager
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.CountdownScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.SentinelViewModel
import com.example.viewmodel.SentinelViewModelFactory
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorDetectionManager
    private lateinit var bleService: BleMeshService

    // In a real app, use Dependency Injection like Hilt
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { EmergencyRepository(database.emergencyLogDao()) }
    
    private val viewModel: SentinelViewModel by viewModels {
        SentinelViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        sensorManager = SensorDetectionManager(this)
        bleService = BleMeshService()
        
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val logs by viewModel.uiState.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                onNavigateToAlerts = { navController.navigate("alerts") },
                                onNavigateToHistory = { navController.navigate("history") },
                                onSimulateFall = { sensorManager.simulateFall() },
                                onManualSOS = { navController.navigate("countdown") }
                            )
                        }
                        composable("countdown") {
                            CountdownScreen(
                                onCancel = {
                                    viewModel.logEmergency("Manual/Fall", "Cancelled", 0.0, 0.0, "Cancelled")
                                    navController.popBackStack()
                                },
                                onTimerFinished = {
                                    viewModel.logEmergency("Sensor Detected", "Critical", 12.97, 77.59, "Sent (BLE)")
                                    bleService.startEmergencyBroadcast("A102", 12.97, 77.59)
                                    navController.popBackStack() // Or navigate to a 'Broadcasting' state
                                }
                            )
                        }
                        composable("alerts") {
                            AlertsScreen(onBack = { navController.popBackStack() })
                        }
                        composable("history") {
                            HistoryScreen(
                                logs = logs,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }

                // Listen for sensor events
                LaunchedEffect(Unit) {
                    sensorManager.fallDetected.onEach {
                        // Navigate to countdown if a fall is detected
                        // Note: navigate calls from LaunchedEffect should be safe if on UI thread
                        if (navController.currentDestination?.route == "home") {
                             navController.navigate("countdown")
                        }
                    }.launchIn(this)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.startMonitoring()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.stopMonitoring()
        bleService.stopEmergencyBroadcast()
    }
}
