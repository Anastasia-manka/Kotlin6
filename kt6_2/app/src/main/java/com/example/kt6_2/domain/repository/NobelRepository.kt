package com.example.kt6_2.domain.repository

import com.example.kt6_2.domain.model.NobelPrize

interface NobelRepository {
    suspend fun getPrizes(limit: Int, year: String?, category: String?): List<NobelPrize>
    suspend fun getPrizeById(id: String): NobelPrize?
}