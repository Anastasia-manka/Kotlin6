package com.example.kt6_2.domain.usecase

import com.example.kt6_2.domain.model.NobelPrize
import com.example.kt6_2.domain.repository.NobelRepository

class GetPrizesUseCase(
    private val repository: NobelRepository
) {
    suspend operator fun invoke(
        limit: Int = 50,
        year: String? = null,
        category: String? = null
    ): List<NobelPrize> = repository.getPrizes(limit, year, category)
}