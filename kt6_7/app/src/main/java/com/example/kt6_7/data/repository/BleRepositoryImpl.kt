package com.example.kt6_7.data.repository

import com.example.kt6_7.data.datasource.BleDataSource
import com.example.kt6_7.domain.model.BleDevice
import com.example.kt6_7.domain.model.ConnectionState
import com.example.kt6_7.domain.model.HeartRateData
import com.example.kt6_7.domain.repository.BleRepository
import kotlinx.coroutines.flow.Flow

class BleRepositoryImpl(
    private val dataSource: BleDataSource
) : BleRepository {

    override fun startScan(): Flow<List<BleDevice>> = dataSource.scanResults

    override fun stopScan() = dataSource.stopScan()

    override fun connectToDevice(address: String): Flow<ConnectionState> = dataSource.connectionState

    override fun disconnect() = dataSource.disconnect()

    override fun subscribeToHeartRate(): Flow<HeartRateData> = dataSource.heartRateData

    override fun getConnectionState(): Flow<ConnectionState> = dataSource.connectionState
}