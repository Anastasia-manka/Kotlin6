package com.example.kt6_3.domain.usecase

import com.example.kt6_3.domain.model.AuthResult
import com.example.kt6_3.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): AuthResult {
        return repository.login(username, password)
    }
}