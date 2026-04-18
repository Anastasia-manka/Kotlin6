package com.example.kt6_3.data.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable


@OptIn(InternalSerializationApi::class)
@Serializable
data class ErrorResponse(
    val message: String,
    val error: String? = null,
    val statusCode: Int? = null
)