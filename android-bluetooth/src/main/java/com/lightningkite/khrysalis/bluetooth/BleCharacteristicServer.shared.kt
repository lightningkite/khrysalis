package com.lightningkite.khrysalis.bluetooth

import com.lightningkite.khrysalis.bytes.Data

//Client implementation

data class BleCharacteristicProperties(
    val broadcast: Boolean = false,
    val read: Boolean = false,
    val writeWithoutResponse: Boolean = false,
    val write: Boolean = false,
    val notify: Boolean = false,
    val indicate: Boolean = false,
    val authenticatedSignedWrites: Boolean = false,
    val extendedProperties: Boolean = false,
    val notifyEncryptionRequired: Boolean = false,
    val indicateEncryptionRequired: Boolean = false,
    val writeEncryptionRequired: Boolean = false
)

interface BleCharacteristicServer {
    val characteristic: BleCharacteristic
    val properties: BleCharacteristicProperties

    fun onSubscribe(from: BleClient)
    fun onUnsubscribe(from: BleClient)
    fun onDisconnect(from: BleClient)
    fun onRead(from: BleClient, request: RequestId)
    fun onWrite(from: BleClient, request: RequestId, value: Data)

    fun notify(client: BleClient, value: Data) = client.notify(characteristic.serviceUuid, characteristic.characteristicUuid, value)
    fun indicate(client: BleClient, value: Data) = client.indicate(characteristic.serviceUuid, characteristic.characteristicUuid, value)
}
