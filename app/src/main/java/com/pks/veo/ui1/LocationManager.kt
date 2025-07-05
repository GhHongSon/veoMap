package com.pks.veo.ui1

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.Granularity.*
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task

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
            Priority.PRIORITY_HIGH_ACCURACY,  // 定位精度优先级
            10000  // 间隔时间（毫秒）
        ).apply {
            setMinUpdateIntervalMillis(5000)  // 最短更新时间（毫秒）
//            // 其他可选设置
            setWaitForAccurateLocation(true)  // 等待精确位置
            setGranularity(GRANULARITY_PERMISSION_LEVEL)  // 精度级别
        }.build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
           Looper.getMainLooper()
        ).addOnFailureListener {
            Log.e("","$it")
        }.addOnSuccessListener {
            Log.e("","$it")
        }
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