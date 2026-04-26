package com.example.kt6_7.domain.usecase

import com.example.kt6_7.domain.repository.BleRepository

class StopScanUseCase(
    private val repository: BleRepository
) {
    operator fun invoke() = repository.stopScan()
}