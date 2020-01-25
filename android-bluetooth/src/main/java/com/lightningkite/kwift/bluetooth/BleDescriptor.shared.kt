package com.lightningkite.kwift.bluetooth

import java.util.*

data class BleDescriptor(
    val serviceUuid: UUID,
    val characteristicUuid: UUID,
    val uuid: UUID
)
