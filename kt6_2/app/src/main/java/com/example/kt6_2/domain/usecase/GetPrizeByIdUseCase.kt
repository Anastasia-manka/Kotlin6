package com.example.kt6_2.domain.usecase

import com.example.kt6_2.domain.model.NobelPrize
import com.example.kt6_2.domain.repository.NobelRepository

class GetPrizeByIdUseCase(
    private val repository: NobelRepository
) {
    suspend operator fun invoke(id: String): NobelPrize? = repository.getPrizeById(id)
}