package com.healthwatch.healthapp.presentation.permissions

import android.Manifest

object PermissionUtils {
    val permissions =
        listOf(
            Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )
}