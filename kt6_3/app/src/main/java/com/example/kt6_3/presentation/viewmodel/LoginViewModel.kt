package com.example.kt6_3.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kt6_3.domain.model.AuthResult
import com.example.kt6_3.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val user: com.example.kt6_3.domain.model.User) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Заполните все поля")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = loginUseCase(username, password)
            _uiState.value = when (result) {
                is AuthResult.Success -> LoginUiState.Success(result.user)
                is AuthResult.Error -> LoginUiState.Error(result.message)
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}