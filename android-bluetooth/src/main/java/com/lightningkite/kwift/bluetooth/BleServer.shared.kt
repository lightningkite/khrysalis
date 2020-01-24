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
    val serviceUuid: UUID
    val uuid: UUID
    val properties: BleCharacteristicProperties

    fun onSubscribe(from: BleClient)
    fun onUnsubscribe(from: BleClient)
    fun onRead(from: BleClient, request: RequestId)
    fun onWrite(from: BleClient, request: RequestId, value: ByteArray)

    fun notify(client: BleClient, value: ByteArray) = client.notify(serviceUuid, uuid, value)
    fun indicate(client: BleClient, value: ByteArray) = client.indicate(serviceUuid, uuid, value)
}

class PropertyBleCharacteristicServer(
    override val serviceUuid: UUID,
    override val uuid: UUID,
    value: ByteArray,
    override val properties: BleCharacteristicProperties = BleCharacteristicProperties(read = true, write = true, notify = true)
): MutableObservableProperty<ByteArray>(), BleCharacteristicServer {

    var underlyingValue: ByteArray = value
    override var value: ByteArray
        get() = underlyingValue
        set(value) {
            underlyingValue = value
            update()
        }
    val subscribers: HashMap<String, BleClient> = HashMap()

    override fun onSubscribe(from: BleClient) {
        subscribers[from.info.id] = from
    }

    override fun onUnsubscribe(from: BleClient) {
        subscribers.remove(from.info.id)
    }

    override fun onRead(from: BleClient, request: RequestId) {
        from.respond(request, value, BleResponseStatus.success)
    }

    override fun onWrite(from: BleClient, request: RequestId, value: ByteArray) {
        this.value = value
        from.respond(request, value, BleResponseStatus.success)
    }

    override fun update() {
        val value = underlyingValue
        underlyingEvent.onNext(boxWrap(value))
        for(sub in subscribers) {
            indicate(sub.value, value)
        }
    }

    val underlyingEvent: PublishSubject<Box<ByteArray>> = PublishSubject.create()
    override val onChange: Observable<Box<ByteArray>>
        get() = underlyingEvent.observeOn(AndroidSchedulers.mainThread())
}

//My implementation

interface BleServer: Disposable {
    val clients: ObservableProperty<Map<String, BleClient>>
    val characteristics: Map<UUID, Map<UUID, BleCharacteristicServer>>
}

interface BleClient {
    val info: BleDeviceInfo
    val connected: Boolean
    fun respond(request: RequestId, data: ByteArray, status: BleResponseStatus)
    /** Does not require a confirmation from the device. **/
    fun notify(service: UUID, characteristic: UUID, value: ByteArray)
    /** Requires a confirmation from the device. **/
    fun indicate(service: UUID, characteristic: UUID, value: ByteArray)
}
