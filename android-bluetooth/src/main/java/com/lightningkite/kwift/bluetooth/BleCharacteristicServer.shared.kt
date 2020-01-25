package com.lightningkite.kwift.bluetooth

import com.lightningkite.kwift.Box
import com.lightningkite.kwift.boxWrap
import com.lightningkite.kwift.observables.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.*

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
    fun onRead(from: BleClient, request: RequestId)
    fun onWrite(from: BleClient, request: RequestId, value: ByteArray)

    fun notify(client: BleClient, value: ByteArray) = client.notify(characteristic.serviceUuid, characteristic.uuid, value)
    fun indicate(client: BleClient, value: ByteArray) = client.indicate(characteristic.serviceUuid, characteristic.uuid, value)
}
