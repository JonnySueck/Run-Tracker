package com.raywenderlich.android.runtracker

import android.Manifest
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class PermissionsManager(
    activity: AppCompatActivity,
    private val locationProvider: LocationProvider,
    private val stepCounter: StepCounter
) {
    // Register a callback on the Activity when the user grants permission
    private val locationPermissionProvider = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { granted ->
            if(granted) {
                locationProvider.getUserLocation()
            }
        }

    // Register a callback that runs when user permits tracking steps
    private val activityRecognitionPermissionProvider =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                stepCounter.setupStepCounter()
            }
        }


    fun requestActivityRecognition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activityRecognitionPermissionProvider.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            stepCounter.setupStepCounter()
        }
    }

    // Function to ask for location permissions. Runs ActivityResultContracts callback when permission is granted
    fun requestUserLocation() {
        locationPermissionProvider.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}