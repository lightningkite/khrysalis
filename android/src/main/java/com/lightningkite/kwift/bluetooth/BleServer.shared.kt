package com.lightningkite.kwift.bluetooth

import java.util.*

interface BleServiceServer {
    val uuid: UUID
    fun onSubscribe(from: BleClient, characteristic: UUID)
    fun onUnsubscribe(from: BleClient, characteristic: UUID)
    fun onRead(from: BleClient, characteristic: UUID)
    fun onWrite(from: BleClient, characteristic: UUID, value: ByteArray)
}

class BleClient
