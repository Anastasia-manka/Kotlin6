package com.example.kt6_3.data.api

import android.util.Log
import com.example.kt6_3.data.dto.LoginRequestDto
import com.example.kt6_3.data.dto.LoginResponseDto
import com.example.kt6_3.data.dto.UserDto
import com.example.kt6_3.data.dto.UsersResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


class AuthApi(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://dummyjson.com"
        private const val TAG = "AuthApi"
    }

    suspend fun login(username: String, password: String): LoginResponseDto {
        val response: HttpResponse = client.post {
            url("$BASE_URL/auth/login")
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(username, password))
        }

        return if (response.status == HttpStatusCode.OK) {
            val json = Json { ignoreUnknownKeys = true }
            json.decodeFromString<LoginResponseDto>(response.body())
        } else {
            val errorText = response.body<String>()
            Log.e(TAG, "Login error: $errorText")
            throw Exception("Неверное имя пользователя или пароль")
        }
    }

    suspend fun getUsers(token: String): UsersResponseDto {
        return client.get {
            url("$BASE_URL/users")
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }.body()
    }

    suspend fun getUserById(token: String, userId: Int): UserDto {
        return client.get {
            url("$BASE_URL/users/$userId")
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }.body()
    }
}