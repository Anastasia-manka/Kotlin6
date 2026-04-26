package com.example.kt6_7.domain.usecase

import com.example.kt6_7.domain.model.HeartRateData
import com.example.kt6_7.domain.repository.BleRepository
import kotlinx.coroutines.flow.Flow

class SubscribeToHeartRateUseCase(
    private val repository: BleRepository
) {
    operator fun invoke(): Flow<HeartRateData> = repository.subscribeToHeartRate()
}