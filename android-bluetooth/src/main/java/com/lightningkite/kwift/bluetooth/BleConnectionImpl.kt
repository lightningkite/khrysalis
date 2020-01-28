package com.lightningkite.kwift.bluetooth

import com.lightningkite.kwift.bytes.Data
import com.polidea.rxandroidble2.RxBleConnection
import io.reactivex.Observable
import io.reactivex.Single

class BleConnectionImpl(val underlying: RxBleConnection, override val deviceInfo: BleDeviceInfo) : BleConnection {

    override val mtu: Int
        get() = underlying.mtu

    override fun read(characteristic: BleCharacteristic): Single<Data> =
        underlying.readCharacteristic(characteristic.characteristicUuid)

    override fun read(descriptor: BleDescriptor): Single<Data> =
        underlying.readDescriptor(descriptor.serviceUuid, descriptor.characteristicUuid, descriptor.descriptorUuid)

    override fun write(characteristic: BleCharacteristic, value: Data): Single<Data> =
        underlying.writeCharacteristic(characteristic.characteristicUuid, value)

    override fun write(descriptor: BleDescriptor, value: Data): Single<Data> =
        underlying.writeCharacteristic(descriptor.descriptorUuid, value)

    override fun notify(characteristic: BleCharacteristic): Observable<Data> =
        underlying.setupNotification(characteristic.characteristicUuid).switchMap { it }

    override fun indicate(characteristic: BleCharacteristic): Observable<Data> =
        underlying.setupIndication(characteristic.characteristicUuid).switchMap { it }

    override fun readRssi(): Single<Int> = underlying.readRssi()

    fun requestMtu(size: Int): Single<Int> = underlying.requestMtu(size)
}
