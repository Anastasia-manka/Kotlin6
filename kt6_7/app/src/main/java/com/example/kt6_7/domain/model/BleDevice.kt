package com.example.kt6_7.domain.model

import android.bluetooth.le.ScanResult

data class BleDevice(
    val address: String,
    val name: String?,
    val rssi: Int,
    val scanRecord: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BleDevice
        return address == other.address
    }

    override fun hashCode(): Int = address.hashCode()
}

data class HeartRateData(
    val heartRate: Int,
    val timestamp: Long = System.currentTimeMillis()
)

sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connecting : ConnectionState()
    object Connected : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}