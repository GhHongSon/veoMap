package com.pks.veo.manager

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.Granularity.*
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

interface LocationUpdateListener {
    fun onLocationUpdated(location: Location)
}

class LocationManager(
    private val context: Context,
    private val listener: LocationUpdateListener
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.locations.forEach { location ->
                listener.onLocationUpdated(location)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        ).apply {
            setMinUpdateIntervalMillis(5000)
            setWaitForAccurateLocation(true)
            setGranularity(GRANULARITY_PERMISSION_LEVEL)
        }.build()
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
           Looper.getMainLooper()
        )
        fusedLocationClient.lastLocation.addOnSuccessListener {
            listener.onLocationUpdated(it)
        }

    }


    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun cleanup() {
        stopLocationUpdates()
    }
}