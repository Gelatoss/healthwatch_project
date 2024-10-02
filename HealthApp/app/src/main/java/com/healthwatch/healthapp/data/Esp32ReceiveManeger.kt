package com.healthwatch.healthapp.data

import android.bluetooth.BluetoothGatt
import com.healthwatch.healthapp.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow

interface Esp32ReceiveManeger {

    val data: MutableSharedFlow<Resource<Esp32Result>>

    fun reconnect()

    fun disconnect()

    fun startReceiving()

    fun closeConnection()

    fun write(message: String)

}