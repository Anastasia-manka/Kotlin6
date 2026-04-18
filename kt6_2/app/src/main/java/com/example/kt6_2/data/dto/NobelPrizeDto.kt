package com.example.kt6_2.data.dto

import kotlinx.serialization.Serializable

import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
@Serializable
data class NobelPrizeResponse(
    val nobelPrizes: List<NobelPrizeDto>
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class NobelPrizeDto(
    val awardYear: String,
    val category: CategoryName,
    val categoryFullName: CategoryName,
    val prizeAmount: Int? = null,
    val prizeAmountAdjusted: Int? = null,
    val laureates: List<LaureateDto>? = null
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class CategoryName(
    val en: String
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class LaureateDto(
    val id: String,
    val knownName: KnownNameDto? = null,
    val fullName: FullNameDto? = null,
    val motivation: MotivationDto? = null,
    val portion: String? = null,
    val birth: BirthPlaceDto? = null,
    val nobelPrizes: List<NobelPrizeRefDto>? = null
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class KnownNameDto(
    val en: String? = null
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class FullNameDto(
    val en: String? = null
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class MotivationDto(
    val en: String? = null
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class BirthPlaceDto(
    val city: CityDto? = null,
    val country: CountryDto? = null
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class CityDto(
    val en: String? = null
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class CountryDto(
    val en: String? = null
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class NobelPrizeRefDto(
    val awardYear: String,
    val category: CategoryName
)