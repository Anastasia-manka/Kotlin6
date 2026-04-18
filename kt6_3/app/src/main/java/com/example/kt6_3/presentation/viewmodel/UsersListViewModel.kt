package com.example.kt6_3.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kt6_3.domain.model.User
import com.example.kt6_3.domain.usecase.GetUsersUseCase
import com.example.kt6_3.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UsersListUiState {
    object Loading : UsersListUiState()
    data class Success(val users: List<User>) : UsersListUiState()
    data class Error(val message: String) : UsersListUiState()
}

class UsersListViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UsersListUiState>(UsersListUiState.Loading)
    val uiState: StateFlow<UsersListUiState> = _uiState.asStateFlow()

    private var _shouldNavigateToLogin = MutableStateFlow(false)
    val shouldNavigateToLogin: StateFlow<Boolean> = _shouldNavigateToLogin.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = UsersListUiState.Loading
            try {
                val users = getUsersUseCase()
                if (users.isNotEmpty()) {
                    _uiState.value = UsersListUiState.Success(users)
                } else {
                    _uiState.value = UsersListUiState.Error("Не удалось загрузить пользователей")
                }
            } catch (e: Exception) {
                _uiState.value = UsersListUiState.Error("Ошибка загрузки: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _shouldNavigateToLogin.value = true
        }
    }

    fun onNavigateToLoginComplete() {
        _shouldNavigateToLogin.value = false
    }
}