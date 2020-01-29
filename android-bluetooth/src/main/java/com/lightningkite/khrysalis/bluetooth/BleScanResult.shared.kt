package com.lightningkite.khrysalis.bluetooth


data class BleScanResult(
    val info: BleDeviceInfo,
    val rssi: Int
)
