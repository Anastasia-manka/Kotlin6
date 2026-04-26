package com.example.kt6_2.data.repository

import android.util.Log
import com.example.kt6_2.data.api.NobelApi
import com.example.kt6_2.domain.model.NobelPrize
import com.example.kt6_2.domain.repository.NobelRepository
import com.example.kt6_2.data.dto.toDomain


class NobelRepositoryImpl(
    private val api: NobelApi
) : NobelRepository {

    override suspend fun getPrizes(
        limit: Int,
        year: String?,
        category: String?
    ): List<NobelPrize> {
        return try {
            val prizes = api.getPrizes()
            Log.d("NobelDebug", "Получено премий: ${prizes.size}")
            prizes.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("NobelDebug", "Ошибка: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getPrizeById(id: String): NobelPrize? {
        val allPrizes = getPrizes(100, null, null)
        return allPrizes.find { it.id == id }
    }
}