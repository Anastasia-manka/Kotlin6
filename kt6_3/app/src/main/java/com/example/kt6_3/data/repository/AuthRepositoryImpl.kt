package com.example.kt6_3.data.repository

import com.example.kt6_3.data.api.AuthApi
import com.example.kt6_3.data.datasource.TokenDataStore
import com.example.kt6_3.data.dto.UserDto
import com.example.kt6_3.domain.model.AuthResult
import com.example.kt6_3.domain.model.User
import com.example.kt6_3.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val tokenDataStore: TokenDataStore
) : AuthRepository {

    override suspend fun login(username: String, password: String): AuthResult {
        return try {
            val response = api.login(username, password)
            tokenDataStore.saveToken(response.accessToken)
            AuthResult.Success(
                User(
                    id = response.id,
                    firstName = response.firstName,
                    lastName = response.lastName,
                    email = response.email,
                    username = response.username,
                    image = response.image
                )
            )
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Ошибка авторизации")
        }
    }

    override fun getToken(): Flow<String?> = tokenDataStore.token

    override suspend fun getUsers(): List<User> {
        val token = tokenDataStore.token.first() ?: return emptyList()
        return try {
            val response = api.getUsers(token)
            response.users.map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getUserById(userId: Int): User? {
        val token = tokenDataStore.token.first() ?: return null
        return try {
            val response = api.getUserById(token, userId)
            response.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun logout() {
        tokenDataStore.clearToken()
    }

    private fun UserDto.toDomain(): User {
        return User(
            id = id,
            firstName = firstName,
            lastName = lastName,
            email = email,
            username = username,
            image = image
        )
    }
}