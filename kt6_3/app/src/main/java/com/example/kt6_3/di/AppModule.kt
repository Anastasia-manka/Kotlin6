package com.example.kt6_3.di

import android.content.Context
import com.example.kt6_3.data.api.AuthApi
import com.example.kt6_3.data.datasource.TokenDataStore
import com.example.kt6_3.data.repository.AuthRepositoryImpl
import com.example.kt6_3.domain.repository.AuthRepository
import com.example.kt6_3.domain.usecase.GetUserByIdUseCase
import com.example.kt6_3.domain.usecase.GetUsersUseCase
import com.example.kt6_3.domain.usecase.LoginUseCase
import com.example.kt6_3.domain.usecase.LogoutUseCase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object AppModule {

    private val httpClient: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }

    fun provideAuthApi(): AuthApi = AuthApi(httpClient)
    fun provideTokenDataStore(context: Context): TokenDataStore = TokenDataStore(context)
    fun provideAuthRepository(context: Context): AuthRepository = AuthRepositoryImpl(
        provideAuthApi(),
        provideTokenDataStore(context)
    )
    fun provideLoginUseCase(context: Context): LoginUseCase = LoginUseCase(provideAuthRepository(context))
    fun provideGetUsersUseCase(context: Context): GetUsersUseCase = GetUsersUseCase(provideAuthRepository(context))
    fun provideGetUserByIdUseCase(context: Context): GetUserByIdUseCase = GetUserByIdUseCase(provideAuthRepository(context))
    fun provideLogoutUseCase(context: Context): LogoutUseCase = LogoutUseCase(provideAuthRepository(context))
}