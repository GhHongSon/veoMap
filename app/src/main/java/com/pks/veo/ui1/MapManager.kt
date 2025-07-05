package com.pks.veo.ui1

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.libraries.navigation.SupportNavigationFragment
import com.pks.veo.R

class MapManager(
    private val context: Context,
    private val container: ViewGroup
) {
    private val navFragment = SupportNavigationFragment.newInstance()
    var mDestinationLatLng: LatLng? = null

    init {
        setupMapFragment()
    }

    private fun setupMapFragment() {
        (context as androidx.fragment.app.FragmentActivity).supportFragmentManager
            .beginTransaction()
            .replace(R.id.root_fl, navFragment)
            .commit()


        navFragment.getMapAsync { googleMap ->
            if (PermissionManager(context).checkLocationPermission()) {
                googleMap.isMyLocationEnabled = true
            }

            googleMap.setOnMapClickListener { latLng ->
                mDestinationLatLng = latLng
                googleMap.clear()
                googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title("Marker")
                )
            }
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
}