package com.lightningkite.kwift.bluetooth

import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleConnection
import io.reactivex.Observable
import io.reactivex.Single

class BleConnectionImpl(val underlying: RxBleConnection, override val deviceInfo: BleDeviceInfo) : BleConnection {

    override val mtu: Int
        get() = underlying.mtu

    override fun read(characteristic: BleCharacteristic): Single<ByteArray> =
        underlying.readCharacteristic(characteristic.uuid)

    override fun read(descriptor: BleDescriptor): Single<ByteArray> =
        underlying.readDescriptor(descriptor.serviceUuid, descriptor.characteristicUuid, descriptor.uuid)

    override fun write(characteristic: BleCharacteristic, value: ByteArray): Single<ByteArray> =
        underlying.writeCharacteristic(characteristic.uuid, value)

    override fun write(descriptor: BleDescriptor, value: ByteArray): Single<ByteArray> =
        underlying.writeCharacteristic(descriptor.uuid, value)

    override fun notify(characteristic: BleCharacteristic): Observable<ByteArray> =
        underlying.setupNotification(characteristic.uuid).switchMap { it }

    override fun indicate(characteristic: BleCharacteristic): Observable<ByteArray> =
        underlying.setupIndication(characteristic.uuid).switchMap { it }

    override fun readRssi(): Single<Int> = underlying.readRssi()

    override fun requestMtu(size: Int): Single<Int> = underlying.requestMtu(size)
}
