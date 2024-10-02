package com.healthwatch.healthapp.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.healthwatch.healthapp.model.SharedPreferencesManager

@Composable
fun Navigation(
    onBluetoothStateChanged:()-> Unit
) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.StartScreen.route){
        composable(Screen.StartScreen.route){
            StartScreen(navController = navController)
        }
        composable(Screen.Esp32Screen.route){
            ESP32Screen(
                onBluetoothStateChanged = onBluetoothStateChanged
            )
        }
    }
}

sealed class Screen(val route:String){
    object StartScreen:Screen("start_screen")
    object Esp32Screen:Screen("ESP32_Screen")
}