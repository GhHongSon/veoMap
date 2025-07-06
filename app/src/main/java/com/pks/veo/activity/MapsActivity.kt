package com.pks.veo.activity

import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pks.veo.databinding.ActivityMapsBinding
import com.pks.veo.manager.LocationManager
import com.pks.veo.manager.LocationUpdateListener
import com.pks.veo.manager.MapClickListener
import com.pks.veo.manager.MapManager
import com.pks.veo.manager.NavigationListener
import com.pks.veo.manager.NavigationManager
import com.pks.veo.manager.PermissionManager
import kotlin.math.roundToInt


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
        initPageState()
    }

    private fun initializeManagers() {
        permissionManager = PermissionManager(this)
        locationManager = LocationManager(this, this)
        mapManager = MapManager(this, this)
        navigationManager = NavigationManager(this, this)
        permissionManager.checkLocationPermission { granted ->
            if (granted) {
                locationManager.getDeviceLastLocation()
            }
        }

    }

    private fun setupClickListeners() {
        binding.btnStart.setOnClickListener {
            permissionManager.checkLocationPermission { granted ->
                if (granted) {
                    mapManager.destinationLatLng?.let { latLng ->
                        navigationManager.startNavigation(latLng)
                    }
                }
            }
        }

        binding.btnStop.setOnClickListener {
            navigationManager.stopNavigation()
        }

        binding.btnFinish.setOnClickListener {
            showViewByState(PageState.SELECT_DESTINATION)
            mapManager.clearMap()
            mapManager.setMyLocationEnabled(true)
        }

    }

    private fun initPageState() {
        showViewByState(PageState.SELECT_DESTINATION)
    }

    override fun onNavigationStarted() {
        showViewByState(PageState.NAVIGATING)
        mapManager.setNavigationUiEnabled(true)
        mapManager.setMyLocationEnabled(true)
        locationManager.stopLocationUpdates()
    }

    override fun onNavigationStopped() {
        showViewByState(PageState.NAVIGATION_END)
        mapManager.setNavigationUiEnabled(false)
        mapManager.setMyLocationEnabled(false)
        navigationManager.navigationData?.let { navData ->
            binding.tvTime.text = navData.totalTime()
            binding.tvDistance.text = "${navData.distance}m"
            navData.path?.let { navDataPath ->
                mapManager.drawTrajectory(navDataPath)
                mapManager.makeAllTrajectoriesVisibleOnTheMap(navDataPath)
            }
        }

    }

    override fun onLocationUpdated(location: Location) {

    }

    override fun onDeviceLastLocation(latLng: LatLng?) {
        if (latLng != null) {
            mapManager.moveCamera(latLng, 18f)
        } else {
            showDefaultLocation()
        }
    }

    private fun showDefaultLocation() {
        val defaultLocation = LatLng(22.32, 114.03)
        mapManager.moveCamera(defaultLocation, 15f)
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

    private fun showViewByState(pageState: PageState) {
        when (pageState) {
            PageState.SELECT_DESTINATION -> {
                binding.tvTip.visibility = View.VISIBLE
                binding.llInfo.visibility = View.GONE
                binding.btnStart.visibility = View.GONE
                binding.btnStop.visibility = View.GONE
                binding.btnFinish.visibility = View.GONE
            }

            PageState.NAVIGATING -> {
                binding.btnStop.visibility = View.VISIBLE
                binding.tvTip.visibility = View.GONE
                binding.llInfo.visibility = View.GONE
                binding.btnStart.visibility = View.GONE
                binding.btnFinish.visibility = View.GONE
            }

            PageState.NAVIGATION_END -> {
                binding.llInfo.visibility = View.VISIBLE
                binding.btnFinish.visibility = View.VISIBLE
                binding.btnStop.visibility = View.GONE
                binding.tvTip.visibility = View.GONE
                binding.btnStart.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.cleanup()
        navigationManager.cleanup()
    }

    enum class PageState {
        SELECT_DESTINATION, NAVIGATING, NAVIGATION_END
    }
}