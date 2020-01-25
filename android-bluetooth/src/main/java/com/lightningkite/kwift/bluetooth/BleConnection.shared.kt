package com.lightningkite.kwift.bluetooth

import io.reactivex.Observable
import io.reactivex.Single

interface BleConnection {
    val deviceInfo: BleDeviceInfo
    val mtu: Int
    fun readRssi(): Single<Int>
    fun requestMtu(size: Int): Single<Int>
    fun read(characteristic: BleCharacteristic): Single<ByteArray>
    fun write(characteristic: BleCharacteristic, value: ByteArray): Single<ByteArray>
    fun notify(characteristic: BleCharacteristic): Observable<ByteArray>
    fun indicate(characteristic: BleCharacteristic): Observable<ByteArray>
    fun read(descriptor: BleDescriptor): Single<ByteArray>
    fun write(descriptor: BleDescriptor, value: ByteArray): Single<ByteArray>
}

