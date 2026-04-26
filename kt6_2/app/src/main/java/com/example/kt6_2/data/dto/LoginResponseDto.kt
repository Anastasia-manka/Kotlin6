package com.example.kt6_2.data.dto
import kotlinx.serialization.Serializable
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
@Serializable
data class LoginResponseDto(
    val token: String,
    val userId: Int,
    val username: String
)