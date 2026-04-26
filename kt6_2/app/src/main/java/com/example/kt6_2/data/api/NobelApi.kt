package com.example.kt6_2.data.api

import com.example.kt6_2.data.dto.LoginRequestDto
import com.example.kt6_2.data.dto.LoginResponseDto
import com.example.kt6_2.data.dto.NobelPrizeDto
import com.example.kt6_2.data.dto.NobelPrizeResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import android.util.Log


class NobelApi(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "http://192.168.1.96:8080"
    }



    // POST /login — авторизация
    suspend fun login(username: String, password: String): LoginResponseDto {
        return client.post("$BASE_URL/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(username, password))
        }.body()
    }

    // GET /users/me/prizes — избранное (требует токен)
    suspend fun getFavorites(token: String): List<NobelPrizeDto> {
        return try {
            client.get("$BASE_URL/users/me/prizes") {
                header("Authorization", "Bearer $token")
            }.body()
        } catch (e: Exception) {
            emptyList()
        }
    }
    suspend fun getPrizes(): List<NobelPrizeDto> {
        val response: String = client.get("$BASE_URL/prizes").body()
        Log.d("NobelDebug", "RAW JSON: $response")
        return Json.decodeFromString(response)
    }
}