package com.example.kt6_7.domain.repository

import com.example.kt6_7.domain.model.BleDevice
import com.example.kt6_7.domain.model.ConnectionState
import com.example.kt6_7.domain.model.HeartRateData
import kotlinx.coroutines.flow.Flow

interface BleRepository {
    fun startScan(): Flow<List<BleDevice>>
    fun stopScan()
    fun connectToDevice(address: String): Flow<ConnectionState>
    fun disconnect()
    fun subscribeToHeartRate(): Flow<HeartRateData>
    fun getConnectionState(): Flow<ConnectionState>
}