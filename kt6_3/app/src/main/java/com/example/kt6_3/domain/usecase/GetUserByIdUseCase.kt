package com.example.kt6_3.domain.usecase

import com.example.kt6_3.domain.model.User
import com.example.kt6_3.domain.repository.AuthRepository

class GetUserByIdUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(userId: Int): User? {
        return repository.getUserById(userId)
    }
}