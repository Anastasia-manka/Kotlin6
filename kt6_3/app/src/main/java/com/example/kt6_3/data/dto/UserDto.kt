package com.example.kt6_3.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
@Serializable
data class UserDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val maidenName: String? = null,
    val age: Int? = null,
    val gender: String? = null,
    val email: String,
    val phone: String? = null,
    val username: String,
    val password: String? = null,
    val birthDate: String? = null,
    val image: String,
    val bloodGroup: String? = null,
    val height: Double? = null,
    val weight: Double? = null,
    val eyeColor: String? = null,
    val hair: HairDto? = null,
    val domain: String? = null,
    val ip: String? = null,
    val address: AddressDto? = null,
    val macAddress: String? = null,
    val university: String? = null,
    val bank: BankDto? = null,
    val company: CompanyDto? = null,
    val ein: String? = null,
    val ssn: String? = null,
    val userAgent: String? = null,
    val role: String? = null,
    val crypto: CryptoDto? = null
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class HairDto(
    val color: String,
    val type: String
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class AddressDto(
    val address: String,
    val city: String,
    val coordinates: CoordinatesDto? = null,
    val postalCode: String,
    val state: String
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class CoordinatesDto(
    val lat: Double,
    val lng: Double
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class BankDto(
    val cardExpire: String,
    val cardNumber: String,
    val cardType: String,
    val currency: String,
    val iban: String
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class CompanyDto(
    val address: AddressDto? = null,
    val department: String,
    val name: String,
    val title: String
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class CryptoDto(
    val coin: String,
    val wallet: String,
    val network: String
)