package com.example.kt6_2.domain.model

data class NobelPrize(
    val id: String,
    val awardYear: String,
    val category: String,
    val categoryFullName: String,
    val laureateName: String,
    val motivation: String?,
    val country: String?,
    val portraitUrl: String?
) {
    val shortMotivation: String
        get() = motivation?.take(100)?.let {
            if (motivation.length > 100) "$it..." else it
        } ?: "Описание отсутствует"
}