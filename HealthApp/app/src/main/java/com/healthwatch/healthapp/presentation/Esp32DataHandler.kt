package com.healthwatch.healthapp.presentation

import com.healthwatch.healthapp.data.Esp32Result
import com.healthwatch.healthapp.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow

interface Esp32DataHandler {
    val data: MutableSharedFlow<Resource<Esp32Result>>

    fun emitData(result: Resource<Esp32Result>)
}