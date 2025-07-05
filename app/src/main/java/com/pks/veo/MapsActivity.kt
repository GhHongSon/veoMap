package com.pks.veo

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.navigation.NavigationApi
import com.google.android.libraries.navigation.NavigationApi.NavigatorListener
import com.google.android.libraries.navigation.Navigator
import com.google.android.libraries.navigation.SupportNavigationFragment
import com.google.android.libraries.navigation.Waypoint


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var mLocationPermissionGranted = false
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        checkSelfPermission()
        initNavigationApi()
        initMap()
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.navigation_map) as SupportNavigationFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
//        mMap.isMyLocationEnabled=true
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.setOnMapClickListener {

//            Navigator.
        }
    }

    private fun checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(
                this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            );
        }

    }

    private fun createWaypoint(latLng: LatLng): Waypoint {
        return Waypoint.builder()
            .setLatLng(latLng.latitude, latLng.longitude)
            .build()
    }

    private fun initNavigationApi() {
        NavigationApi.getNavigator(this, object : NavigatorListener {
            /**
             * Sets up the navigation UI when the navigator is ready for use.
             */
            override fun onNavigatorReady(navigator: Navigator) {
                Log.d("onNavigatorReady", "onNavigatorReady")
//                initMap()
//                navigator.setDestination()
//                navigator.startGuidance()

//                navigator.setDestination(createWaypoint())
            }

            /**
             * Handles errors from the Navigation SDK.
             * @param errorCode The error code returned by the navigator.
             */
            override fun onError(@NavigationApi.ErrorCode errorCode: Int) {

                Log.d("onError", "$errorCode")
            }
        })
    }


}