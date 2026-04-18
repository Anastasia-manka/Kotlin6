package com.example.kt6_1.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kt6_1.domain.GetPhotosUseCase
import com.example.kt6_1.domain.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PhotoUiState {
    object Loading : PhotoUiState()
    data class Success(val photos: List<Photo>) : PhotoUiState()
    data class Error(val message: String) : PhotoUiState()
}

class PhotoViewModel(
    private val getPhotosUseCase: GetPhotosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PhotoUiState>(PhotoUiState.Loading)
    val uiState: StateFlow<PhotoUiState> = _uiState.asStateFlow()

    init {
        loadPhotos()
    }

    fun loadPhotos() {
        viewModelScope.launch {
            _uiState.value = PhotoUiState.Loading
            try {
                val photos = getPhotosUseCase(20)
                if (photos.isNotEmpty()) {
                    _uiState.value = PhotoUiState.Success(photos)
                } else {
                    _uiState.value = PhotoUiState.Error("Фотографии не загружены. Проверьте интернет-соединение.")
                }
            } catch (e: java.net.UnknownHostException) {
                _uiState.value = PhotoUiState.Error("Нет подключения к интернету")
            } catch (e: java.net.SocketTimeoutException) {
                _uiState.value = PhotoUiState.Error("Превышено время ожидания")
            } catch (e: Exception) {
                _uiState.value = PhotoUiState.Error("Ошибка загрузки: ${e.message}")
            }
        }
    }
}