package com.pks.veo.ui1

import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.pks.veo.databinding.ActivityMaps2Binding

class MapsActivity2 : AppCompatActivity(), NavigationListener,
    LocationUpdateListener {

    private lateinit var binding: ActivityMaps2Binding
    private lateinit var permissionManager: PermissionManager
    private lateinit var locationManager: LocationManager
    private lateinit var mapManager: MapManager
    private lateinit var navigationManager: NavigationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaps2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeManagers()
        setupClickListeners()
    }

    private fun initializeManagers() {
        permissionManager = PermissionManager(this)
        locationManager = LocationManager(this, this)
        mapManager = MapManager(this, binding.root)
        navigationManager = NavigationManager(this, this)
        locationManager.startLocationUpdates()
    }

    private fun setupClickListeners() {
        binding.btnStart.setOnClickListener {
            if (permissionManager.checkLocationPermission()) {
                mapManager.mDestinationLatLng?.let { it1 -> navigationManager.startNavigation(it1) }
                locationManager.startLocationUpdates()
            }
        }

        binding.btnStop.setOnClickListener {
            navigationManager.stopNavigation()
        }
    }

    // NavigationListener implementation
    override fun onNavigationStarted() {
        // Handle navigation start events
    }

    override fun onNavigationStopped() {
        // Handle navigation stop events
        navigationManager.navigationData?.let {
            binding.tvTime.text = it.totalTime()
            binding.tvDistance.text = "${it.distance}m"

            it.path?.let { mapManager.drawTrajectory(it) }
        }


    }

    // LocationUpdateListener implementation
    override fun onLocationUpdated(location: Location) {
        mapManager.moveCamera(LatLng(location.latitude, location.longitude), 18f)
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.cleanup()
        navigationManager.cleanup()
    }
}