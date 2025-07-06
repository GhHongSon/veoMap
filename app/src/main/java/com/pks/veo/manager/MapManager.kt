package com.pks.veo.manager

import android.R.attr.padding
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
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
    var destinationLatLng: LatLng? = null
    var destinationMarker: Marker? = null
    var googleMap: GoogleMap? = null

    init {
        setupMapFragment()
    }

    private fun setupMapFragment() {
        (context as androidx.fragment.app.FragmentActivity).supportFragmentManager
            .beginTransaction()
            .replace(R.id.root_fl, navFragment)
            .commit()


        navFragment.getMapAsync { map ->
            this.googleMap = map
            if (PermissionManager(context).checkLocationPermission()) {
                setMyLocationEnabled(true)
            }

            map.setOnMapClickListener { latLng ->
                destinationLatLng = latLng
                mapClickListener.onMapClick(latLng)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun setMyLocationEnabled(enabled: Boolean) {
        googleMap?.isMyLocationEnabled = enabled
    }

    fun addDestinationMark() {
        destinationLatLng?.let {
            destinationMarker?.remove()
            destinationMarker = googleMap?.addMarker(
                MarkerOptions()
                    .position(it)
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
        }
    }

    fun drawTrajectory(locations: List<LatLng>) {
        destinationMarker?.remove()
        googleMap?.let { map ->
            locations.firstOrNull()?.let {
                map.addMarker(
                    MarkerOptions()
                        .position(LatLng(it.latitude, it.longitude))
                        .title("Start")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                )
            }
            locations.lastOrNull()?.let {
                map.addMarker(
                    MarkerOptions()
                        .position(LatLng(it.latitude, it.longitude))
                        .title("End")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )
            }

            val polylineOptions = PolylineOptions().apply {
                color(Color.BLUE)
                width(20f)
                startCap(RoundCap())
                endCap(RoundCap())
                jointType(JointType.ROUND)
                addAll(locations)
            }
            map.addPolyline(polylineOptions)

        }


    }

    fun moveCamera(latLng: LatLng, zoom: Float) {
        navFragment.getMapAsync { map ->
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        }
    }

    fun clearMap() {
        googleMap?.run {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
            clear()
        }

    }

    fun setNavigationUiEnabled(enabled: Boolean) {
        navFragment.isNavigationUiEnabled = enabled
    }


    fun makeAllTrajectoriesVisibleOnTheMap(path: List<LatLng>?) {
        if (path.isNullOrEmpty()) {
            return
        }
        googleMap?.let { map ->
            val boundsBuilder = LatLngBounds.Builder()
            path.forEach {
                boundsBuilder.include(it)
            }
            val bounds = boundsBuilder.build()
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
            map.moveCamera(cameraUpdate)
        }

    }
}