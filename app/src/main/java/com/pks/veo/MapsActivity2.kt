//package com.pks.veo
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.location.Location
//import android.location.LocationManager
//import android.os.Bundle
//import android.os.Looper
//import android.util.Log
//import android.widget.Toast
//import androidx.annotation.NonNull
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationAvailability
//import com.google.android.gms.location.LocationCallback
//import com.google.android.gms.location.LocationRequest
//import com.google.android.gms.location.LocationResult
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.Marker
//import com.google.android.gms.maps.model.MarkerOptions
//import com.google.android.gms.maps.model.PolylineOptions
//import com.google.android.gms.tasks.OnSuccessListener
//import com.google.android.libraries.navigation.NavigationApi
//import com.google.android.libraries.navigation.Navigator
//import com.google.android.libraries.navigation.Navigator.RouteStatus
//import com.google.android.libraries.navigation.RoadSnappedLocationProvider
//import com.google.android.libraries.navigation.RoutingOptions
//import com.google.android.libraries.navigation.SimulationOptions
//import com.google.android.libraries.navigation.SupportNavigationFragment
//import com.google.android.libraries.navigation.Waypoint
//import com.google.android.libraries.navigation.Waypoint.UnsupportedPlaceIdException
//import com.pks.veo.databinding.ActivityMaps2Binding
//
//class MapsActivity2 : AppCompatActivity(), RoadSnappedLocationProvider.LocationListener {
//    private val TAG: String = MapsActivity2::class.java.getSimpleName()
//    private var mNavigator: Navigator? = null
//
//
//    private val mNavFragment: SupportNavigationFragment by lazy {
//        SupportNavigationFragment.newInstance()
//    }
//
//    private val mMapFragment: SupportMapFragment by lazy {
//        SupportMapFragment.newInstance()
//    }
//
//    //    private lateinit var mapFragment: SupportMapFragment
////    private lateinit var navigationFragment: SupportNavigationFragment
//    private var mGoogleMap: GoogleMap? = null
//    private var mDestinationLatLng: LatLng? = null
//    private var mDestinationMarker: Marker? = null
//    private var mRoutingOptions: RoutingOptions? = null
//    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: Int = 1
//    private var mLocationPermissionGranted = false
//    private lateinit var binding: ActivityMaps2Binding
//    private var mRoadSnappedLocationProvider: RoadSnappedLocationProvider? = null
//    private val mLocationList: MutableList<Location> by lazy {
//        mutableListOf()
//    }
//
//    private val fusedLocationClient: FusedLocationProviderClient by lazy {
//        LocationServices.getFusedLocationProviderClient(this@MapsActivity2)
//    }
//
//    private val mLocationCallback: LocationCallback by lazy {
//
//        object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                super.onLocationResult(locationResult)
//                locationResult.locations.forEach { location ->
//                    // 处理位置更新
//                    val latitude = location.getLatitude();
//                    val longitude = location.getLongitude();
//                    val speed = location.getSpeed(); // 速度(米/秒)
//                    val bearing = location.getBearing(); // 方向(度)
//
//                    // 这些信息可以用于导航
//                    Log.d(
//                        "LocationUpdate", "Lat: " + latitude + ", Lng: " + longitude +
//                                ", Speed: " + speed + ", Bearing: " + bearing
//                    );
//                    mLocationList.add(location)
//                }
//
//
//            }
//
//            override fun onLocationAvailability(p0: LocationAvailability) {
//                super.onLocationAvailability(p0)
//            }
//        }
//    }
//
//    val requiredPermissions = arrayOf(
//        Manifest.permission.FOREGROUND_SERVICE_LOCATION,
//        Manifest.permission.ACCESS_FINE_LOCATION
//    )
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMaps2Binding.inflate(layoutInflater)
//        setContentView(binding.root)
//        if (checkRequestPermission()) {
//            mLocationPermissionGranted = true
//        }
//        initMap()
//        viewOnClick()
//        supportFragmentManager.beginTransaction().replace(R.id.root_fl, mNavFragment).commit()
//
//    }
//
//
//    private fun startLocationUpdates() {
//        val locationRequest: LocationRequest = LocationRequest.create()
//        locationRequest.setInterval(10000) // 更新间隔(毫秒)
//        locationRequest.setFastestInterval(5000) // 最快更新间隔
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // 高精度
//
//
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return;
//        }
//        fusedLocationClient.requestLocationUpdates(
//            locationRequest,
//            mLocationCallback,
//            Looper.getMainLooper()
//        );
//    }
//
//    private fun stopLocationUpdates() {
//
//        fusedLocationClient.removeLocationUpdates(mLocationCallback)
//            .addOnSuccessListener(OnSuccessListener<Void> { aVoid: Void? ->
//                Log.d("Location", "Location updates stopped")
//            })
//    }
//
//
//    private fun viewOnClick() {
//        binding.btnStart.setOnClickListener {
////            if (mNavigator?.isGuidanceRunning != true) {
//            displayMessage("开始导航")
//            mLocationList.clear()
////            startLocationUpdates()
////            }
//            initNavigationSdk()
//
//        }
//
//        binding.btnStop.setOnClickListener {
//            exitNavigation()
//            drawTrajectory()
//        }
//
//    }
//
//    /**
//     * 绘制行程轨迹
//     */
//    private fun drawTrajectory() {
//        val polylineOptions = PolylineOptions()
//        val ll = mLocationList.map {
//            LatLng(it.latitude, it.longitude)
//        }
//        polylineOptions.addAll(ll)
//
//        mNavFragment.getMapAsync { map ->
//            map?.addPolyline(polylineOptions)
//        }
//
//    }
//
//    private fun backMapView() {
//        supportFragmentManager.beginTransaction().replace(R.id.root_fl, mMapFragment).commit()
//        getMyLocation()
//    }
//
//    private fun checkRequestPermission(): Boolean {
//        val denied = requiredPermissions.any {
//            ContextCompat.checkSelfPermission(
//                this.applicationContext, it
//            ) == PackageManager.PERMISSION_DENIED
//        }
//        if (denied) {
//            ActivityCompat.requestPermissions(
//                this,
//                requiredPermissions,
//                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
//            );
//        }
//        return !denied
//    }
//
//    private fun initNavigationSdk() {
//        if (!mLocationPermissionGranted) {
//            Log.e(
//                TAG,
//                "Error loading Navigation SDK: " + "The user has not granted location permission."
//            )
//            return;
//        }
//
//        NavigationApi.getNavigator(this@MapsActivity2, object : NavigationApi.NavigatorListener {
//
//            override fun onNavigatorReady(navigator: Navigator?) {
//                mNavigator = navigator;
//
//                mNavFragment?.getMapAsync { googleMap ->
//                    if (ActivityCompat.checkSelfPermission(
//                            this@MapsActivity2,
//                            Manifest.permission.ACCESS_FINE_LOCATION
//                        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                            this@MapsActivity2,
//                            Manifest.permission.ACCESS_COARSE_LOCATION
//                        ) == PackageManager.PERMISSION_GRANTED
//                    ) {
//                        googleMap.followMyLocation(GoogleMap.CameraPerspective.TILTED)
//                    }
//
//                }
//
//                mRoutingOptions = RoutingOptions().apply {
//                    travelMode(RoutingOptions.TravelMode.DRIVING)
//                }
//                navigateToPlace(mRoutingOptions!!)
//
//                mNavigator?.addArrivalListener { arrivalEvent ->
//                    Log.e(TAG, "shun0->$arrivalEvent")
//                }
//                mRoadSnappedLocationProvider =
//                    NavigationApi.getRoadSnappedLocationProvider(this@MapsActivity2.application)
//                mRoadSnappedLocationProvider?.addLocationListener(this@MapsActivity2)
//            }
//
//            override fun onError(errorCode: Int) {
//                when (errorCode) {
//                    NavigationApi.ErrorCode.NOT_AUTHORIZED -> displayMessage(
//                        "Error loading Navigation SDK: Your API key is "
//                                + "invalid or not authorized to use the Navigation SDK."
//                    )
//
//                    NavigationApi.ErrorCode.TERMS_NOT_ACCEPTED -> displayMessage(
//                        "Error loading Navigation SDK: User did not accept "
//                                + "the Navigation Terms of Use."
//                    )
//
//                    NavigationApi.ErrorCode.NETWORK_ERROR -> displayMessage("Error loading Navigation SDK: Network error.")
//                    NavigationApi.ErrorCode.LOCATION_PERMISSION_MISSING -> displayMessage(
//                        "Error loading Navigation SDK: Location permission " + "is missing."
//                    )
//
//                    else -> displayMessage("Error loading Navigation SDK: $errorCode")
//                }
//            }
//
//        })
//
//    }
//
//
//    private fun initMap() {
//        val szbzLa = LatLng(22.60, 114.02)
//        mNavFragment.getMapAsync { googleMap ->
//            mGoogleMap = googleMap
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(szbzLa, 15f))
//            googleMap.setOnMapClickListener { latLng ->
//                mDestinationLatLng = latLng
//                mDestinationMarker?.remove()
//
//                mDestinationMarker = googleMap.addMarker(
//                    MarkerOptions()
//                        .position(latLng)
//                        .title("Marker")
//                )
//            }
//            Log.e(TAG, "$googleMap")
//        }
//
//    }
//
//    private fun createWaypoint(latLng: LatLng): Waypoint {
//        return Waypoint.builder()
//            .setLatLng(latLng.latitude, latLng.longitude)
//            .build()
//    }
//
//    private fun exitNavigation() {
//        mNavigator?.traveledRoute?.let {
//            val distance=calculateDistance(it)
//            binding.tvDistance.text="$distance 米"
//        }
//
//        // 1. 停止导航指导
//        mNavigator?.stopGuidance()
//
//
//
//        // 2. 停止模拟器（如果调试模式启用）
////        if (BuildConfig.DEBUG) {
////            mNavigator?.getSimulator()?.stop()
////        }
//
//        // 3. 清除导航目的地
//        mNavigator?.clearDestinations()
//        mRoadSnappedLocationProvider?.resetFreeNav()
//        mRoadSnappedLocationProvider?.removeLocationListener(this)
//        // 4. 恢复地图状态
////        restoreMapView()
//
//        // 5. 恢复界面元素
//        actionBar?.show()
//        mNavigator?.unregisterServiceForNavUpdates()
//        mNavigator=null
//    }
//
//    private fun navigateToPlace(travelMode: RoutingOptions) {
////        val destination: Waypoint = createWaypoint(latLng = LatLng(22.60, 114.029))
//
//        var destination: Waypoint? = null
//
//        try {
//            mDestinationLatLng?.let {
//                destination = createWaypoint(it)
//            }
////            destination = Waypoint.builder().setPlaceIdString(placeId).build()
//        } catch (e: UnsupportedPlaceIdException) {
//            displayMessage("Error starting navigation: Place ID is not supported.")
//            return
//        }
//
//        // Create a future to await the result of the asynchronous navigator task.
//        val pendingRoute =
//            mNavigator!!.setDestination(destination, travelMode)
//
//
//        // Define the action to perform when the SDK has determined the route.
//        pendingRoute.setOnResultListener { code ->
//            when (code) {
//                RouteStatus.OK -> {
//                    // Hide the toolbar to maximize the navigation UI.
//                    if (actionBar != null) {
//                        actionBar!!.hide()
//                    }
//
//                    // Enable voice audio guidance (through the device speaker).
//                    mNavigator!!.setAudioGuidance(Navigator.AudioGuidance.VOICE_ALERTS_AND_GUIDANCE)
////                    mNavigator!!.setAudioGuidance(Navigator.AudioGuidance.SILENT)
//
//                    // Simulate vehicle progress along the route for demo/debug builds.
////                    if (BuildConfig.DEBUG) {
////                        mNavigator!!.getSimulator()
////                            .simulateLocationsAlongExistingRoute(
////                                SimulationOptions().speedMultiplier(5f)
////                            )
////                    }
//
//                    // Start turn-by-turn guidance along the current route.
//                    mNavigator!!.startGuidance()
//                }
//
//                RouteStatus.NO_ROUTE_FOUND -> displayMessage("Error starting navigation: No route found.")
//                RouteStatus.NETWORK_ERROR -> displayMessage("Error starting navigation: Network error.")
//                RouteStatus.ROUTE_CANCELED -> displayMessage("Error starting navigation: Route canceled.")
//                else -> displayMessage("Error starting navigation: $code")
//            }
//        }
//    }
//
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        mLocationPermissionGranted = false
//        when (requestCode) {
//            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
//                // If request is canceled, the result arrays are empty.
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    mLocationPermissionGranted = true
////                    initNavigationSdk()
//                }
//            }
//        }
//    }
//
//    /**
//     * Shows a message on screen and in the log. Used when something goes wrong.
//     *
//     * @param errorMessage The message to display.
//     */
//    private fun displayMessage(errorMessage: String) {
//        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
//        Log.d(TAG, errorMessage)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mRoadSnappedLocationProvider?.removeLocationListener(this)
//    }
//
//    override fun onLocationChanged(location: Location?) {
//        displayMessage("保存位置：${location}")
//        location?.let {
//            mLocationList.add(location)
//        }
//    }
//
//    override fun onRawLocationUpdate(location: Location?) {
//
//    }
//
//    override fun onPause() {
//        super.onPause()
//        fusedLocationClient.removeLocationUpdates(mLocationCallback)
//    }
//
//    private fun getMyLocation() {
//        val manager = getSystemService(LOCATION_SERVICE) as? LocationManager ?: return
//        try {
//            // Request location updates
//            manager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER,
//                1000L,
//                1f,
//                { location -> handleLocationUpdate(location) }
//            )
//
//        } catch (e: SecurityException) {
//            Log.e(TAG, "Location permission not granted", e)
//        } catch (e: Exception) {
//            Log.e(TAG, "Error getting location", e)
//        }
//    }
//
//    private fun handleLocationUpdate(location: Location) {
//        val myLocation = LatLng(location.latitude, location.longitude)
//        runOnUiThread {
//            mMapFragment.getMapAsync { map ->
//                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
//            }
//
//        }
//    }
//
//
//    public fun calculateDistance(path: List<LatLng>): Float {
//
//        if (path.size < 2) return 0f
//
//        var totalDistance = 0f
//        for (i in 0 until path.size - 1) {
//            val point1 = path[i]
//            val point2 = path[i + 1]
//
//            val results = FloatArray(1)
//            Location.distanceBetween(
//                point1.latitude, point1.longitude,
//                point2.latitude, point2.longitude,
//                results
//            )
//            totalDistance += results[0]
//        }
//        return totalDistance // 返回以米为单位的距离
//    }
//}
