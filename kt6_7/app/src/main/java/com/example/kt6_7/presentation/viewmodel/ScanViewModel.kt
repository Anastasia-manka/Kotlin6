package com.example.kt6_7.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kt6_7.domain.model.BleDevice
import com.example.kt6_7.domain.usecase.StartScanUseCase
import com.example.kt6_7.domain.usecase.StopScanUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed class ScanUiState {
    object Idle : ScanUiState()
    object Scanning : ScanUiState()
    data class Results(val devices: List<BleDevice>) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}

class ScanViewModel(
    private val startScanUseCase: StartScanUseCase,
    private val stopScanUseCase: StopScanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    fun startScan() {
        _uiState.value = ScanUiState.Scanning
        startScanUseCase()
            .onEach { devices ->
                _uiState.value = ScanUiState.Results(devices)
            }
            .launchIn(viewModelScope)
    }

    fun stopScan() {
        stopScanUseCase()
        if (_uiState.value is ScanUiState.Scanning) {
            _uiState.value = ScanUiState.Idle
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopScan()
    }
}