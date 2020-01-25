package com.lightningkite.kwift.bluetooth


data class BleScanResult(
    val info: BleDeviceInfo,
    val rssi: Int
)
