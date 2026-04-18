package com.example.kt6_2.data.api

import com.example.kt6_2.data.dto.NobelPrizeResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class NobelApi(
    private val client: HttpClient
) {
    suspend fun getNobelPrizes(
        limit: Int = 50,
        year: String? = null,
        category: String? = null
    ): NobelPrizeResponse {
        return client.get("https://api.nobelprize.org/2.1/nobelPrizes") {
            parameter("limit", limit)
            year?.let {
                parameter("nobelPrizeYear", it)
                parameter("yearTo", it)
            }
            category?.let { parameter("nobelPrizeCategory", it) }
        }.body()
    }
}