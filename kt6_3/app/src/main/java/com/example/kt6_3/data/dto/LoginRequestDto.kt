package com.example.kt6_3.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String
)