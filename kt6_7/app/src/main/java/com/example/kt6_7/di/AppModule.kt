package com.example.kt6_7.di

import android.content.Context
import com.example.kt6_7.data.datasource.BleDataSource
import com.example.kt6_7.data.repository.BleRepositoryImpl
import com.example.kt6_7.domain.repository.BleRepository
import com.example.kt6_7.domain.usecase.*
import kotlinx.coroutines.flow.Flow

object AppModule {

    private lateinit var bleDataSource: BleDataSource
    private lateinit var bleRepository: BleRepository

    fun init(context: Context) {
        bleDataSource = BleDataSource(context.applicationContext)
        bleRepository = BleRepositoryImpl(bleDataSource)
    }

    fun provideBleRepository(): BleRepository = bleRepository

    fun provideStartScanUseCase(): StartScanUseCase = StartScanUseCase(provideBleRepository())
    fun provideStopScanUseCase(): StopScanUseCase = StopScanUseCase(provideBleRepository())
    fun provideConnectToDeviceUseCase(): ConnectToDeviceUseCase = ConnectToDeviceUseCase(provideBleRepository())
    fun provideDisconnectDeviceUseCase(): DisconnectDeviceUseCase = DisconnectDeviceUseCase(provideBleRepository())
    fun provideSubscribeToHeartRateUseCase(): SubscribeToHeartRateUseCase = SubscribeToHeartRateUseCase(provideBleRepository())
}