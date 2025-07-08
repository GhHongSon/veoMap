VEO Map - Google Maps Navigation Implementation

[![Google Navigation API](https://img.shields.io/badge/API-Google_Navigation-4285F4?logo=google-maps&logoColor=white)](https://developers.google.com/maps/documentation/navigation/overview)


An Android navigation solution implementing Google Maps functionality including destination selection, real-time navigation, and trip analytics.


<img src="gif/demonstration_effect.gif" alt="demonstrate" width="150" height="300" loop="infinite">

## ✨ Key Features
- 🎯 Intelligent destination selection (map click)
- 🚗 Real-time navigation with voice guidance
- 📊 Post-trip analytics (distance/duration)
- 📍 Automatic correction for Google Maps coordinate offset

### 1. Page Component
- **1.1 Map Interaction**  
  Handles all user interactions with the map interface including:
  - Destination selection
  - Navigation controls
  - Map gesture responses
 

### 2. Manager Components
- **2.1 Location Manager**  
  - Obtains device location using GPS/Network providers
  - Provides continuous position updates
  - Handles location permission scenarios
  
- **2.2 Map Manager**  
  - Initializes Google Maps API
  - Renders markers for start/end points
  - Draws navigation path polyline
  - Manages map camera movements


  
- **2.3 Navigation Manager**  
  - Calculates optimal routes
  - Provides turn-by-turn instructions
  - Monitors navigation state changes
  - Triggers arrival events
  
- **2.4 Permission Manager**  
  - Checks required Android permissions
  - Handles runtime permission requests
  - Manages permission denial scenarios

### 3. Utility Classes
- **`CoordinateConverter`**  
  Solves China-specific coordinate system deviation 

### 4. Code Snippets

- Obtain user device location information
   ```kotlin
   fun getDeviceLastLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val correctedLatLng = CoordinateConverter.wgs84ToGcj02(
                            location.latitude,
                            location.longitude
                        )
                        listener.onDeviceLastLocation(correctedLatLng)
                    } else {
                        listener.onDeviceLastLocation(null)
                    }
                }

        } catch (e: SecurityException) {
            listener.onDeviceLastLocation(null)
        }
    }
  ```


- Add destination marker to the map
  ```kotlin
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

  ```

- Navigation to destination
  ```kotlin
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
  ```
- Draw path trajectory
  ```kotlin
   val polylineOptions = PolylineOptions().apply {
                color(Color.BLUE)
                width(20f)
                startCap(RoundCap())
                endCap(RoundCap())
                jointType(JointType.ROUND)
                addAll(locations)
            }
   map.addPolyline(polylineOptions)
  ```

