package com.raywenderlich.android.runtracker

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_FASTEST
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData

// Provides access to the step counter permission and tracks steps with live data
class StepCounter(private val activity: AppCompatActivity): SensorEventListener {
    val liveSteps = MutableLiveData<Int>()

    private val sensorManager by lazy {
        activity.getSystemService(SENSOR_SERVICE) as SensorManager
    }

    private val stepCounterSensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    private var initialSteps = -1
    override fun onSensorChanged(event: SensorEvent?) {
        event?.values?.firstOrNull()?.toInt()?.let { newSteps ->
            if(initialSteps == -1) {
                // Recording has not started
                initialSteps = newSteps
            }
            // Recalculate the difference between the new data and initial steps
            val currentSteps = newSteps - initialSteps
            // Set the live data to the current steps
            liveSteps.value = currentSteps
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    fun setupStepCounter() {
        if(stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SENSOR_DELAY_FASTEST)
        }
    }

    fun unloadStepCounter() {
        if (stepCounterSensor != null) {
            sensorManager.unregisterListener(this)
        }
    }
}
