package com.example.kt6_3.domain.repository

import com.example.kt6_3.domain.model.AuthResult
import com.example.kt6_3.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(username: String, password: String): AuthResult
    fun getToken(): Flow<String?>
    suspend fun getUsers(): List<User>
    suspend fun getUserById(userId: Int): User?
    suspend fun logout()
}