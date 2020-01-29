package com.lightningkite.khrysalis.bluetooth

import java.util.*

data class BleCharacteristic(
    val serviceUuid: UUID,
    val characteristicUuid: UUID
)
