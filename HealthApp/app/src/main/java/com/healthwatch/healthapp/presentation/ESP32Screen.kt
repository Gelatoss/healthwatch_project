package com.healthwatch.healthapp.presentation

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.healthwatch.healthapp.data.ConnectionState
import com.healthwatch.healthapp.presentation.permissions.PermissionUtils
import com.healthwatch.healthapp.presentation.permissions.SystemBroadcastReceiver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.healthwatch.healthapp.model.SharedPreferencesManager
import com.healthwatch.healthapp.view.HomeActivity

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ESP32Screen(
    onBluetoothStateChanged: () -> Unit,
    viewModel: Esp32ViewModel = hiltViewModel()
) {
    SystemBroadcastReceiver(systemAction = BluetoothAdapter.ACTION_STATE_CHANGED) { bluetoothState ->
        val action = bluetoothState?.action ?: return@SystemBroadcastReceiver
        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            onBluetoothStateChanged()
        }
    }

    val context = LocalContext.current
    val permissionState = rememberMultiplePermissionsState(permissions = PermissionUtils.permissions)
    val lifecycleOwner = LocalLifecycleOwner.current
    val bleConnectionState = viewModel.connectionState

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                permissionState.launchMultiplePermissionRequest()
                if (permissionState.allPermissionsGranted && bleConnectionState == ConnectionState.Disconnected) {
                    viewModel.reconnect()
                }
            }
            if (event == Lifecycle.Event.ON_STOP) {
                if (bleConnectionState == ConnectionState.Connected) {
                    viewModel.disconnect()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(key1 = permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) {
            if (bleConnectionState == ConnectionState.Uninitialized) {
                viewModel.initializeConnection()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(600.dp) // Set height explicitly to ensure the content fits
                .border(BorderStroke(7.dp, Color.Green), RoundedCornerShape(10.dp))
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Make the column scrollable
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                bleConnectionState == ConnectionState.CurrentlyInitializing -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                        viewModel.initializingMessage?.let {
                            Text(text = it)
                        }
                    }
                }
                !permissionState.allPermissionsGranted -> {
                    Text(
                        text = "Go to the app setting and allow the missing permissions.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(10.dp),
                        textAlign = TextAlign.Center
                    )
                }
                viewModel.errorMessage != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = viewModel.errorMessage!!)
                        Button(onClick = {
                            if (permissionState.allPermissionsGranted) {
                                viewModel.initializeConnection()
                            }
                        }) {
                            Text("Try Again")
                        }
                    }
                }
                bleConnectionState == ConnectionState.Connected -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        var message1 by remember { mutableStateOf("") }
                        var message2 by remember { mutableStateOf("") }
                        var message3 by remember { mutableStateOf("") }
                        var message4 by remember { mutableStateOf("") }

                        OutlinedTextField(
                            value = message1,
                            label = { Text("Enter SSID") },
                            onValueChange = { message1 = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        OutlinedTextField(
                            value = message2,
                            label = { Text("Enter Internet Password") },
                            onValueChange = { message2 = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        OutlinedTextField(
                            value = message3,
                            label = { Text("Enter Email") },
                            onValueChange = { message3 = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        OutlinedTextField(
                            value = message4,
                            label = { Text("Enter Password") },
                            onValueChange = { message4 = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        Button(
                            onClick = {
                                if (message1.isNotEmpty() && message2.isNotEmpty() && message3.isNotEmpty() && message4.isNotEmpty()) {
                                    viewModel.sendMessages(
                                        message1,
                                        message2,
                                        message3,
                                        message4
                                    )
                                } else {
                                    Log.e("ESP32Screen", "All messages are required")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Send Messages")
                        }
                    }
                }
                bleConnectionState == ConnectionState.Disconnected -> {
                    Button(onClick = {
                        viewModel.initializeConnection()
                    }) {
                        Text("Initialize Again")
                    }
                }
            }
        }
        Button(
            onClick = {
                context.startActivity(Intent(context, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text("Home Button")
        }
    }
}
