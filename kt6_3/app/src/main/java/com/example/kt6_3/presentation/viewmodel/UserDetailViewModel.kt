package com.example.kt6_3.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kt6_3.domain.model.User
import com.example.kt6_3.domain.usecase.GetUserByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UserDetailUiState {
    object Loading : UserDetailUiState()
    data class Success(val user: User) : UserDetailUiState()
    data class Error(val message: String) : UserDetailUiState()
}

class UserDetailViewModel(
    private val getUserByIdUseCase: GetUserByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserDetailUiState>(UserDetailUiState.Loading)
    val uiState: StateFlow<UserDetailUiState> = _uiState.asStateFlow()

    fun loadUser(userId: Int) {
        viewModelScope.launch {
            _uiState.value = UserDetailUiState.Loading
            try {
                val user = getUserByIdUseCase(userId)
                if (user != null) {
                    _uiState.value = UserDetailUiState.Success(user)
                } else {
                    _uiState.value = UserDetailUiState.Error("Пользователь не найден")
                }
            } catch (e: Exception) {
                _uiState.value = UserDetailUiState.Error("Ошибка загрузки: ${e.message}")
            }
        }
    }
}