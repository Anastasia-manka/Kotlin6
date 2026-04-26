package com.example.kt6_7.domain.usecase

import com.example.kt6_7.domain.model.ConnectionState
import com.example.kt6_7.domain.repository.BleRepository
import kotlinx.coroutines.flow.Flow

class ConnectToDeviceUseCase(
    private val repository: BleRepository
) {
    operator fun invoke(address: String): Flow<ConnectionState> = repository.connectToDevice(address)
}