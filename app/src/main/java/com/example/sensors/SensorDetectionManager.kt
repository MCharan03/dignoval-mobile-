package com.example.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.math.sqrt

class SensorDetectionManager(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    private val _fallDetected = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)
    val fallDetected: SharedFlow<Unit> = _fallDetected.asSharedFlow()

    private var isMonitoring = false
    private val FALL_THRESHOLD = 3.0f // 3G force for impact simulation
    private val FREE_FALL_THRESHOLD = 2.0f // Gravity dropping near zero
    private var isFalling = false
    private var lastFallTime = 0L

    fun startMonitoring() {
        if (!isMonitoring && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            isMonitoring = true
        }
    }

    fun stopMonitoring() {
        if (isMonitoring) {
            sensorManager.unregisterListener(this)
            isMonitoring = false
        }
    }

    fun simulateFall() {
        val now = System.currentTimeMillis()
        if (now - lastFallTime > 3000) {
            _fallDetected.tryEmit(Unit)
            lastFallTime = now
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val gForce = sqrt((x * x + y * y + z * z).toDouble()) / SensorManager.GRAVITY_EARTH
            
            if (gForce < FREE_FALL_THRESHOLD / SensorManager.GRAVITY_EARTH) {
                isFalling = true
            }
            
            // If there's an impact spike immediately after a freefall phase or extreme acceleration
            if (gForce > 2.5) {
                val now = System.currentTimeMillis()
                if (now - lastFallTime > 3000) { // 3 seconds debounce
                    _fallDetected.tryEmit(Unit)
                    lastFallTime = now
                }
                isFalling = false // reset
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
