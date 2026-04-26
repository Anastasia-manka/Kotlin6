package com.example.kt6_7.data.dto

// DTO для парсинга данных пульса (по стандарту BLE)
data class HeartRateDto(
    val heartRate: Int,
    val contactDetected: Boolean = false,
    val energyExpended: Int? = null,
    val rrIntervals: List<Int>? = null
) {
    companion object {
        fun fromByteArray(data: ByteArray): HeartRateDto {
            val flags = data[0].toInt() and 0xFF
            val heartRateFormat = flags and 0x01
            val contactDetected = (flags and 0x02) != 0 && (flags and 0x04) != 0

            var offset = 1
            val heartRate = if (heartRateFormat == 0) {
                data[offset].toInt() and 0xFF
            } else {
                (data[offset].toInt() and 0xFF) or ((data[offset + 1].toInt() and 0xFF) shl 8)
            }.also { offset += if (heartRateFormat == 0) 1 else 2 }

            val energyExpended = if ((flags and 0x08) != 0) {
                (data[offset].toInt() and 0xFF) or ((data[offset + 1].toInt() and 0xFF) shl 8)
            } else null
            offset += if ((flags and 0x08) != 0) 2 else 0

            val rrIntervals = mutableListOf<Int>()
            while ((flags and 0x10) != 0 && offset + 1 < data.size) {
                rrIntervals.add((data[offset].toInt() and 0xFF) or ((data[offset + 1].toInt() and 0xFF) shl 8))
                offset += 2
            }

            return HeartRateDto(
                heartRate = heartRate,
                contactDetected = contactDetected,
                energyExpended = energyExpended,
                rrIntervals = rrIntervals
            )
        }
    }
}