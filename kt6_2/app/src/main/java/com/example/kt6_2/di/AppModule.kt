package com.example.kt6_2.di

import com.example.kt6_2.data.api.NobelApi
import com.example.kt6_2.data.repository.NobelRepositoryImpl
import com.example.kt6_2.domain.repository.NobelRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object AppModule {

    private val httpClient: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }

    val api: NobelApi by lazy { NobelApi(httpClient) }
    val repository: NobelRepository by lazy { NobelRepositoryImpl(api) }
}