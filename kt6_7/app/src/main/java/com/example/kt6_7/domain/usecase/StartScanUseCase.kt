package com.example.kt6_7.domain.usecase

import com.example.kt6_7.domain.model.BleDevice
import com.example.kt6_7.domain.repository.BleRepository
import kotlinx.coroutines.flow.Flow

class StartScanUseCase(
    private val repository: BleRepository
) {
    operator fun invoke(): Flow<List<BleDevice>> = repository.startScan()
}