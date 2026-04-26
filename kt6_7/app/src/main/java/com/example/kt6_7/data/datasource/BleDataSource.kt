package com.example.kt6_7.data.datasource

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import com.example.kt6_7.domain.model.BleDevice
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.UUID
import android.annotation.SuppressLint

@SuppressLint("MissingPermission")
class BleDataSource(private val context: Context) {

    companion object {
        // Heart Rate Service UUID
        val HEART_RATE_SERVICE_UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
        // Heart Rate Measurement Characteristic UUID
        val HEART_RATE_MEASUREMENT_UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
        // Client Characteristic Configuration Descriptor UUID
        val CCC_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }

    private val bluetoothManager: BluetoothManager by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }
    private val bluetoothAdapter: BluetoothAdapter by lazy { bluetoothManager.adapter }

    private var bluetoothGatt: BluetoothGatt? = null
    private var scanCallback: ScanCallback? = null

    private val _scanResults = Channel<List<BleDevice>>(Channel.RENDEZVOUS)
    val scanResults: Flow<List<BleDevice>> = _scanResults.receiveAsFlow()

    private val discoveredDevices = mutableMapOf<String, BleDevice>()

    private val _connectionState = Channel<com.example.kt6_7.domain.model.ConnectionState>(Channel.RENDEZVOUS)
    val connectionState: Flow<com.example.kt6_7.domain.model.ConnectionState> = _connectionState.receiveAsFlow()

    private val _heartRateData = Channel<com.example.kt6_7.domain.model.HeartRateData>(Channel.RENDEZVOUS)
    val heartRateData: Flow<com.example.kt6_7.domain.model.HeartRateData> = _heartRateData.receiveAsFlow()

    fun startScan() {
        discoveredDevices.clear()
        _scanResults.trySend(emptyList())

        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device = result.device
                val bleDevice = BleDevice(
                    address = device.address,
                    name = device.name ?: "Неизвестное устройство",
                    rssi = result.rssi,
                    scanRecord = result.scanRecord?.bytes
                )
                discoveredDevices[device.address] = bleDevice
                _scanResults.trySend(discoveredDevices.values.toList())
            }

            override fun onScanFailed(errorCode: Int) {
                _connectionState.trySend(com.example.kt6_7.domain.model.ConnectionState.Error("Scan failed: $errorCode"))
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bluetoothAdapter.bluetoothLeScanner.startScan(scanCallback)
        } else {
            @Suppress("DEPRECATION")
            bluetoothAdapter.startLeScan(scanCallbackCompat)
        }
    }

    @Suppress("DEPRECATION")
    private val scanCallbackCompat = object : BluetoothAdapter.LeScanCallback {
        override fun onLeScan(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray?) {
            val bleDevice = BleDevice(
                address = device.address,
                name = device.name ?: "Неизвестное устройство",
                rssi = rssi,
                scanRecord = scanRecord
            )
            discoveredDevices[device.address] = bleDevice
            _scanResults.trySend(discoveredDevices.values.toList())
        }
    }

    fun stopScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            scanCallback?.let { bluetoothAdapter.bluetoothLeScanner.stopScan(it) }
        } else {
            @Suppress("DEPRECATION")
            bluetoothAdapter.stopLeScan(scanCallbackCompat)
        }
        scanCallback = null
    }

    fun connectToDevice(address: String) {
        _connectionState.trySend(com.example.kt6_7.domain.model.ConnectionState.Connecting)

        val device = bluetoothAdapter.getRemoteDevice(address)
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        _connectionState.trySend(com.example.kt6_7.domain.model.ConnectionState.Disconnected)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothGatt.STATE_CONNECTED -> {
                    _connectionState.trySend(com.example.kt6_7.domain.model.ConnectionState.Connected)
                    gatt.discoverServices()
                }
                BluetoothGatt.STATE_DISCONNECTED -> {
                    _connectionState.trySend(com.example.kt6_7.domain.model.ConnectionState.Disconnected)
                }
                else -> {
                    _connectionState.trySend(com.example.kt6_7.domain.model.ConnectionState.Error("Connection failed"))
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val heartRateService = gatt.getService(HEART_RATE_SERVICE_UUID)
                heartRateService?.let { service ->
                    val characteristic = service.getCharacteristic(HEART_RATE_MEASUREMENT_UUID)
                    characteristic?.let { char ->
                        enableNotifications(gatt, char)
                    }
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (characteristic.uuid == HEART_RATE_MEASUREMENT_UUID) {
                val data = characteristic.value
                if (data != null && data.isNotEmpty()) {
                    val heartRateData = com.example.kt6_7.data.dto.HeartRateDto.fromByteArray(data)
                    _heartRateData.trySend(
                        com.example.kt6_7.domain.model.HeartRateData(
                            heartRate = heartRateData.heartRate
                        )
                    )
                }
            }
        }
    }

    private fun enableNotifications(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        gatt.setCharacteristicNotification(characteristic, true)

        val descriptor = characteristic.getDescriptor(CCC_DESCRIPTOR_UUID)
        descriptor?.let {
            it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(it)
        }
    }
}