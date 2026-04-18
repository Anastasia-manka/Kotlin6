package com.example.kt6_3.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
@Serializable
data class UsersResponseDto(
    val users: List<UserDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)