package com.example.kt6_2.data.repository

import android.util.Log
import com.example.kt6_2.data.api.NobelApi
import com.example.kt6_2.domain.model.NobelPrize
import com.example.kt6_2.domain.repository.NobelRepository

class NobelRepositoryImpl(
    private val api: NobelApi
) : NobelRepository {

    override suspend fun getPrizes(
        limit: Int,
        year: String?,
        category: String?
    ): List<NobelPrize> {
        return try {
            val response = api.getNobelPrizes(limit, year, category)

            response.nobelPrizes.flatMap { prize ->
                prize.laureates?.mapNotNull { laureate ->
                    val name = laureate.knownName?.en
                        ?: laureate.fullName?.en
                        ?: return@mapNotNull null

                    NobelPrize(
                        id = laureate.id,
                        awardYear = prize.awardYear,
                        category = prize.category.en,
                        categoryFullName = prize.categoryFullName.en,
                        laureateName = name,
                        motivation = laureate.motivation?.en,
                        country = laureate.birth?.country?.en,
                        portraitUrl = "https://picsum.photos/id/${laureate.id.toIntOrNull()?.rem(100) ?: 1}/200/200"
                    )
                } ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("NobelDebug", "Ошибка: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getPrizeById(id: String): NobelPrize? {
        val allPrizes = getPrizes(100, null, null)
        return allPrizes.find { it.id == id }
    }
}