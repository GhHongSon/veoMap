package com.pks.veo.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity.*
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pks.veo.utils.CoordinateConverter

interface LocationUpdateListener {
    fun onLocationUpdated(location: Location)
    fun onDeviceLastLocation(latLng: LatLng?)
}

class LocationManager(
    private val context: Context,
    private val listener: LocationUpdateListener
) {
    private val fusedLocationClient: FusedLocationProviderClient  by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.lastLocation?.let { location ->
                listener.onLocationUpdated(location)
            }
        }
    }

    fun  getDeviceLastLocation(){
        try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            val correctedLatLng = CoordinateConverter.wgs84ToGcj02(
                                location.latitude,
                                location.longitude
                            )
                            listener.onDeviceLastLocation(correctedLatLng)
                        } else {
                            listener.onDeviceLastLocation(null)
                        }
                    }

        } catch (e: SecurityException) {
            listener.onDeviceLastLocation(null)
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