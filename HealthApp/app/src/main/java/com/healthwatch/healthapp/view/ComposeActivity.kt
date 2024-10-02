package com.healthwatch.healthapp.view

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.healthwatch.healthapp.presentation.Navigation
import com.healthwatch.healthapp.ui.theme.ESPApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ComposeActivity : ComponentActivity() {

    @Inject lateinit var bluetoothAdapter: BluetoothAdapter

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission", "Granted")
                showBluetoothDialog()
            } else {
                Log.i("Permission", "Denied")
                // Handle permission denial appropriately
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ESPApplicationTheme {
                Navigation(
                    onBluetoothStateChanged = {
                        checkBluetoothPermission()
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkBluetoothPermission()
    }

    private fun checkBluetoothPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is granted, proceed with Bluetooth operations
                showBluetoothDialog()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.BLUETOOTH_CONNECT) -> {
                // Explain why the permission is needed and request it
                // Show UI or dialog explaining the need for permission
                requestPermissionLauncher.launch(android.Manifest.permission.BLUETOOTH_CONNECT)
            }
            else -> {
                // Directly request the permission
                requestPermissionLauncher.launch(android.Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
    }

    private var isBluetoothDialogAlreadyShown = false
    private fun showBluetoothDialog() {
        if (!bluetoothAdapter.isEnabled) {
            if (!isBluetoothDialogAlreadyShown) {
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startBluetoothIntentForResult.launch(enableBluetoothIntent)
                isBluetoothDialogAlreadyShown = true
            }
        }
    }

    private val startBluetoothIntentForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            isBluetoothDialogAlreadyShown = false
            if (result.resultCode != Activity.RESULT_OK) {
                showBluetoothDialog()
            }
        }
}
