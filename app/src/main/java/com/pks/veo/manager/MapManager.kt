package com.pks.veo.manager

import android.content.Context
import android.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.libraries.navigation.SupportNavigationFragment
import com.pks.veo.R


interface MapClickListener {
    fun onMapClick(latLng: LatLng)
}


class MapManager(
    private val context: Context,
    private val mapClickListener: MapClickListener
) {
    private val navFragment = SupportNavigationFragment.newInstance()
    var mDestinationLatLng: LatLng? = null
    var mGoogleMap: GoogleMap? = null

    init {
        setupMapFragment()
    }

    private fun setupMapFragment() {
        (context as androidx.fragment.app.FragmentActivity).supportFragmentManager
            .beginTransaction()
            .replace(R.id.root_fl, navFragment)
            .commit()


        navFragment.getMapAsync { googleMap ->
            mGoogleMap = googleMap
            if (PermissionManager(context).checkLocationPermission()) {
                googleMap.isMyLocationEnabled = true
            }

            googleMap.setOnMapClickListener { latLng ->
                mDestinationLatLng = latLng
                mapClickListener.onMapClick(latLng)
            }
        }
    }

    fun addDestinationMark() {
        mDestinationLatLng?.let {
            mGoogleMap?.clear()
            mGoogleMap?.addMarker(
                MarkerOptions()
                    .position(it)
                    .title("目的地")
            )
        }
    }

    fun drawTrajectory(locations: List<LatLng>) {
        val polylineOptions = PolylineOptions().apply {
            color(Color.BLUE)
            width(20f)
            startCap(RoundCap())
            endCap(RoundCap())
            jointType(JointType.ROUND)
            addAll(locations)
        }

        navFragment.getMapAsync { map ->
            map?.addPolyline(polylineOptions)
        }
    }

    fun moveCamera(latLng: LatLng, zoom: Float) {
        navFragment.getMapAsync { map ->
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        }
    }

    fun clearMap() {
        mGoogleMap?.run {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
            clear()
        }

    }

    fun setNavigationUiEnabled(enabled: Boolean) {
        navFragment.isNavigationUiEnabled = false
    }
}