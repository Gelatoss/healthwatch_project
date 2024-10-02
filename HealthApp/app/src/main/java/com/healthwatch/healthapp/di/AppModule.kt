package com.healthwatch.healthapp.di
 import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.healthwatch.healthapp.data.Esp32ReceiveManeger
import com.healthwatch.healthapp.data.ble.Esp32BLEReceiveManager
 import com.healthwatch.healthapp.model.SharedPreferencesManager
 import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBluetoothAdapter(@ApplicationContext context: Context):BluetoothAdapter{
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return manager.adapter
    }

    @Provides
    @Singleton
    fun provideEsp32ReceiveManger(
        @ApplicationContext context: Context,
        bluetoothAdapter: BluetoothAdapter
    ):Esp32ReceiveManeger{
        return Esp32BLEReceiveManager(bluetoothAdapter, context)
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesManager(@ApplicationContext context: Context): SharedPreferencesManager {
        return SharedPreferencesManager(context)
    }
}