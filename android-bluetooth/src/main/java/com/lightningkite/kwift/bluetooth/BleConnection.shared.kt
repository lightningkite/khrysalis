package com.lightningkite.kwift.bluetooth

import com.lightningkite.kwift.bytes.Data
import io.reactivex.Observable
import io.reactivex.Single

interface BleConnection {
    val deviceInfo: BleDeviceInfo
    val mtu: Int
    fun readRssi(): Single<Int>
    fun read(characteristic: BleCharacteristic): Single<Data>
    fun write(characteristic: BleCharacteristic, value: Data): Single<Data>
    fun notify(characteristic: BleCharacteristic): Observable<Data>
    fun indicate(characteristic: BleCharacteristic): Observable<Data>
    fun read(descriptor: BleDescriptor): Single<Data>
    fun write(descriptor: BleDescriptor, value: Data): Single<Data>
}

