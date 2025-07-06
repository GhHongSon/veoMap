package com.pks.veo.manager

import android.app.Activity
import android.location.Location
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.navigation.NavigationApi
import com.google.android.libraries.navigation.Navigator
import com.google.android.libraries.navigation.RoadSnappedLocationProvider
import com.google.android.libraries.navigation.RoutingOptions
import com.google.android.libraries.navigation.Waypoint

interface NavigationListener {
    fun onNavigationStarted()
    fun onNavigationStopped()
}

class NavigationManager(
    private val context: Activity,
    private val listener: NavigationListener
) {
    private var navigator: Navigator? = null
    private var roadSnappedLocationProvider: RoadSnappedLocationProvider? = null
    var navigationData: NavigationData? = null
    var navigationStartTime: Long? = null

    fun startNavigation(destinationLatLng: LatLng) {
        NavigationApi.getNavigator(context, object : NavigationApi.NavigatorListener {
            override fun onNavigatorReady(navigator: Navigator?) {
                this@NavigationManager.navigator = navigator
                setupNavigation(destinationLatLng)
                listener.onNavigationStarted()
            }

            override fun onError(errorCode: Int) {
                handleNavigationError(errorCode)
            }
        })
    }

    private fun setupNavigation(destinationLatLng: LatLng) {
        navigator?.let { nav ->
            val routingOptions = RoutingOptions().apply {
                travelMode(RoutingOptions.TravelMode.DRIVING)
            }
            roadSnappedLocationProvider =
                NavigationApi.getRoadSnappedLocationProvider(context.application)
            nav.addArrivalListener { _ ->
                stopNavigation()
            }
            val destination: Waypoint = createWaypoint(destinationLatLng)
            navigateToPlace(destination, routingOptions)
        }
    }

    private fun createWaypoint(latLng: LatLng): Waypoint {
        return Waypoint.builder()
            .setLatLng(latLng.latitude, latLng.longitude)
            .build()
    }

    private fun navigateToPlace(destination: Waypoint, travelMode: RoutingOptions) {
        val pendingRoute =
            navigator?.setDestination(destination, travelMode)
        pendingRoute?.setOnResultListener { code ->
            when (code) {
                Navigator.RouteStatus.OK -> {
                    navigator?.setAudioGuidance(Navigator.AudioGuidance.VOICE_ALERTS_AND_GUIDANCE)
                    navigator?.startGuidance()
                    navigationStartTime = System.currentTimeMillis()
                }

                Navigator.RouteStatus.NO_ROUTE_FOUND -> displayMessage("Error starting navigation: No route found.")
                Navigator.RouteStatus.NETWORK_ERROR -> displayMessage("Error starting navigation: Network error.")
                Navigator.RouteStatus.ROUTE_CANCELED -> displayMessage("Error starting navigation: Route canceled.")
                else -> displayMessage("Error starting navigation: $code")
            }
        }
    }

    fun isGuidanceRunning(): Boolean {
        return navigator?.isGuidanceRunning ?: false
    }

    fun stopNavigation() {
        handleNavigationData()
        navigator?.apply {
            stopGuidance()
            clearDestinations()
            unregisterServiceForNavUpdates()
            removeArrivalListener { }
            setAudioGuidance(Navigator.AudioGuidance.SILENT)
        }

        roadSnappedLocationProvider?.resetFreeNav()
        listener.onNavigationStopped()
        navigator = null
        roadSnappedLocationProvider = null
        navigationStartTime = null

    }


    private fun handleNavigationData() {
        navigator?.traveledRoute?.let {
            navigationData = NavigationData().apply {
                endTime = System.currentTimeMillis()
                path = it
                distance = calculateDistance(it)
                startTime = navigationStartTime
            }
        }
    }

    private fun calculateDistance(path: List<LatLng>): Float {
        if (path.size < 2) return 0f
        var totalDistance = 0f
        for (i in 0 until path.size - 1) {
            val point1 = path[i]
            val point2 = path[i + 1]
            val results = FloatArray(1)
            Location.distanceBetween(
                point1.latitude, point1.longitude,
                point2.latitude, point2.longitude,
                results
            )
            totalDistance += results[0]
        }
        return totalDistance
    }

    private fun handleNavigationError(errorCode: Int) {

    }

    fun cleanup() {
        stopNavigation()
        navigator = null
        roadSnappedLocationProvider = null
    }

    private fun displayMessage(errorMessage: String) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        Log.d("TAG", errorMessage)
    }
}

class NavigationData {
    var distance: Float? = null
    var startTime: Long? = null
    var endTime: Long? = null
    var path: List<LatLng>? = null

    fun totalTime(): String? {
        if (startTime == null || endTime == null) {
            return null
        }
        val totalSeconds = (endTime!! - startTime!!) / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}