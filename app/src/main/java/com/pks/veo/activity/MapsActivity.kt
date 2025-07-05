package com.pks.veo.activity

import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.pks.veo.databinding.ActivityMapsBinding
import com.pks.veo.manager.LocationManager
import com.pks.veo.manager.LocationUpdateListener
import com.pks.veo.manager.MapClickListener
import com.pks.veo.manager.MapManager
import com.pks.veo.manager.NavigationListener
import com.pks.veo.manager.NavigationManager
import com.pks.veo.manager.PermissionManager


class MapsActivity : AppCompatActivity(), NavigationListener,
    LocationUpdateListener, MapClickListener {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var permissionManager: PermissionManager
    private lateinit var locationManager: LocationManager
    private lateinit var mapManager: MapManager
    private lateinit var navigationManager: NavigationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeManagers()
        setupClickListeners()
    }

    private fun initializeManagers() {
        permissionManager = PermissionManager(this)
        locationManager = LocationManager(this, this)
        mapManager = MapManager(this, this)
        navigationManager = NavigationManager(this, this)
        permissionManager.checkLocationPermission { granted ->
            if (granted) {
                locationManager.startLocationUpdates()
            }
        }

    }

    private fun setupClickListeners() {
        binding.btnStart.setOnClickListener {
            permissionManager.checkLocationPermission { granted ->
                if (granted) {
                    mapManager.mDestinationLatLng?.let { it1 ->
                        navigationManager.startNavigation(it1)
                    }
                }
            }
        }

        binding.btnStop.setOnClickListener {
            navigationManager.stopNavigation()
            binding.tvTip.visibility = View.GONE
        }

        binding.btnFinish.setOnClickListener {
            binding.llInfo.visibility = View.GONE
            binding.btnStart.visibility = View.GONE
            binding.btnStop.visibility = View.GONE
            binding.btnFinish.visibility = View.GONE
            binding.tvTip.visibility = View.VISIBLE
            mapManager.clearMap()
        }

    }

    override fun onNavigationStarted() {
//        mapManager.setNavigationUiEnabled(true)
        locationManager.stopLocationUpdates()
        binding.btnStart.visibility = View.GONE
        binding.tvTip.visibility = View.GONE
        binding.btnStop.visibility = View.VISIBLE
    }

    override fun onNavigationStopped() {
//        mapManager.setNavigationUiEnabled(false)
        locationManager.startLocationUpdates()
        binding.btnFinish.visibility = View.VISIBLE
        binding.llInfo.visibility = View.VISIBLE
        binding.btnStop.visibility = View.GONE
        navigationManager.navigationData?.let {
            binding.tvTime.text = it.totalTime()
            binding.tvDistance.text = "${it.distance}m"
            it.path?.let { mapManager.drawTrajectory(it) }
        }

    }

    override fun onLocationUpdated(location: Location) {
        mapManager.moveCamera(LatLng(location.latitude, location.longitude), 18f)
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.cleanup()
        navigationManager.cleanup()
    }


    override fun onMapClick(latLng: LatLng) {
        if (navigationManager.isGuidanceRunning()) {
            return
        }
        mapManager.addDestinationMark()
        binding.btnStart.visibility = View.VISIBLE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}