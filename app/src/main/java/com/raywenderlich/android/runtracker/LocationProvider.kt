package com.raywenderlich.android.runtracker

import android.annotation.SuppressLint
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlin.math.roundToInt

class LocationProvider(private val activity: AppCompatActivity) {
    private val client by lazy {
        LocationServices.getFusedLocationProviderClient(activity)
    }
    private val locations = mutableListOf<LatLng>()
    private var distance = 0
    val liveLocation = MutableLiveData<LatLng>()
    val liveLocations = MutableLiveData<List<LatLng>>()
    val liveDistance = MutableLiveData<Int>()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val currentLocation = result.lastLocation
            // get the recorded location and conver to LatLng
            val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
            // Check if there are other locations to calculate distance
            val lastLocation = locations.lastOrNull()

            if(lastLocation != null) {
                // Current location is not the first location;
                // Uses SphericalUtil to compute distance between two points
                distance += SphericalUtil.computeDistanceBetween(lastLocation, latLng).roundToInt()
                // Add the distance to the liveDistance variable
                liveDistance.value = distance
            }
            // Add the current location to locations
            locations.add(latLng)
            // Emit to most recent location
            liveLocations.value = locations
        }
    }

    // Creates a request to track the users location
    @SuppressLint("MissingPermission")
    fun trackUser() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Track user going at a slow speed
        locationRequest.interval = 5000 // Sets the update interval to five seconds
        client.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    // Clears the data from client and resets distance back to 0
    fun stopTracking() {
        client.removeLocationUpdates(locationCallback)
        locations.clear()
        distance = 0
    }

    @SuppressLint("MissingPermission")
    fun getUserLocation() {
        client.lastLocation.addOnSuccessListener { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            locations.add(latLng)
            liveLocation.value = latLng
        }
    }
}