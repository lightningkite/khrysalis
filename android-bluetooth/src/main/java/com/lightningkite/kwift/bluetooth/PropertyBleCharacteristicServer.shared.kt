package com.lightningkite.kwift.bluetooth

import com.lightningkite.kwift.Box
import com.lightningkite.kwift.boxWrap
import com.lightningkite.kwift.observables.MutableObservableProperty
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import java.util.*


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
