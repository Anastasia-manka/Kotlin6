package com.example.kt6_7.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kt6_7.domain.model.ConnectionState
import com.example.kt6_7.domain.model.HeartRateData
import com.example.kt6_7.domain.usecase.ConnectToDeviceUseCase
import com.example.kt6_7.domain.usecase.DisconnectDeviceUseCase
import com.example.kt6_7.domain.usecase.SubscribeToHeartRateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed class HeartRateUiState {
    object Idle : HeartRateUiState()
    object Connecting : HeartRateUiState()
    object Connected : HeartRateUiState()
    object Disconnected : HeartRateUiState()
    data class Monitoring(val heartRate: Int) : HeartRateUiState()
    data class Error(val message: String) : HeartRateUiState()
}

class HeartRateViewModel(
    private val connectToDeviceUseCase: ConnectToDeviceUseCase,
    private val disconnectDeviceUseCase: DisconnectDeviceUseCase,
    private val subscribeToHeartRateUseCase: SubscribeToHeartRateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HeartRateUiState>(HeartRateUiState.Idle)
    val uiState: StateFlow<HeartRateUiState> = _uiState.asStateFlow()

    private var currentDeviceAddress: String? = null

    fun connectToDevice(address: String) {
        currentDeviceAddress = address
        _uiState.value = HeartRateUiState.Connecting

        connectToDeviceUseCase(address)
            .onEach { state ->
                when (state) {
                    is ConnectionState.Connected -> {
                        _uiState.value = HeartRateUiState.Connected
                        subscribeToHeartRate()
                    }
                    is ConnectionState.Connecting -> _uiState.value = HeartRateUiState.Connecting
                    is ConnectionState.Disconnected -> _uiState.value = HeartRateUiState.Disconnected
                    is ConnectionState.Error -> _uiState.value = HeartRateUiState.Error(state.message)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun subscribeToHeartRate() {
        subscribeToHeartRateUseCase()
            .onEach { data ->
                _uiState.value = HeartRateUiState.Monitoring(heartRate = data.heartRate)
            }
            .launchIn(viewModelScope)
    }

    fun disconnect() {
        disconnectDeviceUseCase()
        currentDeviceAddress = null
        _uiState.value = HeartRateUiState.Disconnected
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}