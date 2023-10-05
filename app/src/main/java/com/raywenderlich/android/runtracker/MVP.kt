package com.raywenderlich.android.runtracker

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng

/* Creates info needed to render UI properly. Includes the distance the user walks,
   their current location, and the list of locations that the app recorded */
data class Ui(
    val formattedPace: String,
    val formattedDistance: String,
    val currentLocation: LatLng?,
    val userPath: List<LatLng>
) {

    companion object {

        val EMPTY = Ui(
            formattedPace = "",
            formattedDistance = "",
            currentLocation = null,
            userPath = emptyList()
        )
    }

    class MapPresenter(private val activity: AppCompatActivity) {
        val ui = MutableLiveData(EMPTY)
        private val locationProvider = LocationProvider(activity)
        private val stepCounter = StepCounter(activity)
        private val permissionsManager = PermissionsManager(activity, locationProvider, stepCounter)

        fun onViewCreated() {
            // creates listener for LOCATIONS and updates the UI
            locationProvider.liveLocations.observe(activity) { locations ->
                val current = ui.value
                ui.value = current?.copy(userPath = locations)
            }
            // Creates listener for the CURRENT LOCATION and updates the UI
            locationProvider.liveLocation.observe(activity) { currentLocation ->
                val current = ui.value
                ui.value = current?.copy(currentLocation = currentLocation)
            }
            // Creates listener for the DISTANCE and updates the UI
            locationProvider.liveDistance.observe(activity) { distance ->
                val current = ui.value
                val formattedDistance = activity.getString(R.string.distance_value, distance)
                ui.value = current?.copy(formattedDistance = formattedDistance)
            }
            // Create listener for STEPS and update UI
            stepCounter.liveSteps.observe(activity) { steps ->
                val current = ui.value
                ui.value = current?.copy(formattedPace = "$steps")
            }
        }

        fun onMapLoaded() {
            permissionsManager.requestUserLocation()
        }

        fun startTracking() {
            locationProvider.trackUser()
            permissionsManager.requestActivityRecognition()
        }

        fun stopTracking() {
            locationProvider.stopTracking()
            stepCounter.unloadStepCounter()
        }
    }
}

