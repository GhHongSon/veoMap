package com.pks.veo.manager

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest

class PermissionManager(private val activity: Activity) {
  private  var requestPermissionsResultListener: ((granted:Boolean) -> Unit)? = null

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.FOREGROUND_SERVICE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    fun checkLocationPermission(resultListener: ((granted:Boolean) -> Unit)? = null): Boolean {
        requestPermissionsResultListener=resultListener
        val allGranted = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
        if (!allGranted) {
            ActivityCompat.requestPermissions(
                activity,
                REQUIRED_PERMISSIONS,
                PERMISSION_REQUEST_CODE
            )
        }else{
            requestPermissionsResultListener?.invoke(true)
        }
        return allGranted
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val granted = grantResults.isNotEmpty() &&
                    grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            requestPermissionsResultListener?.invoke(granted)
        }

    }
}