package com.healthwatch.healthapp.presentation

import android.telephony.mbms.StreamingServiceInfo
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthwatch.healthapp.data.ConnectionState
import com.healthwatch.healthapp.data.Esp32ReceiveManeger
import com.healthwatch.healthapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class Esp32ViewModel @Inject constructor(
    private val esp32ReceiveManeger: Esp32ReceiveManeger
) : ViewModel(){

    var initializingMessage by mutableStateOf<String?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var command by mutableStateOf("")
        private set

    var connectionState by mutableStateOf<ConnectionState>(ConnectionState.Uninitialized)

    private fun subscribeToChanges(){
        viewModelScope.launch{
            esp32ReceiveManeger.data.collect{result ->
                Log.d("class", "receivingData")
                when(result){
                    is Resource.Success -> {
                        connectionState = result.data.connectionState
                        command = result.data.command
                    }

                    is Resource.Loading -> {
                        initializingMessage = result.message
                        connectionState = ConnectionState.CurrentlyInitializing
                    }

                    is Resource.Error -> {
                        errorMessage = result.errorMessage
                        connectionState = ConnectionState.Uninitialized
                    }

                    is Resource.Message ->{
                        connectionState = ConnectionState.Connected
                        esp32ReceiveManeger.write(result.data.command)
                    }
                }
            }
        }

    }

    fun sendMessages(message1: String, message2: String, message3: String, message4: String) {
        viewModelScope.launch {
            esp32ReceiveManeger.write("$message1,$message2,$message3,$message4")
        }
    }

    fun disconnect(){
        esp32ReceiveManeger.disconnect()
    }

    fun reconnect(){
        esp32ReceiveManeger.reconnect()
    }

    fun initializeConnection(){
        errorMessage = null
        subscribeToChanges()
        //subscribeToChanges2()
        esp32ReceiveManeger.startReceiving()
    }

    override fun onCleared() {
        super.onCleared()
        esp32ReceiveManeger.closeConnection()
    }

}