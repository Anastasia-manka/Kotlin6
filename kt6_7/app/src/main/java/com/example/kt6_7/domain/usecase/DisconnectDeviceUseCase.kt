package com.example.kt6_7.domain.usecase

import com.example.kt6_7.domain.repository.BleRepository

class DisconnectDeviceUseCase(
    private val repository: BleRepository
) {
    operator fun invoke() = repository.disconnect()
}