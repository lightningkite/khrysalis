package com.lightningkite.kwift.bluetooth

import android.annotation.TargetApi
import android.bluetooth.*
import android.content.Context
import android.os.Build
import com.lightningkite.kwift.Failable
import com.lightningkite.kwift.async.DRF
import com.lightningkite.kwift.observables.EnablingEvent
import com.lightningkite.kwift.observables.Event
import com.lightningkite.kwift.observables.ObservableProperty
import com.lightningkite.kwift.observables.StandardObservableProperty
import com.lightningkite.kwift.post
import java.util.*

interface BleDevice {
    val connected: ObservableProperty<Boolean>
    val rssi: ObservableProperty<Int>
    val mtu: ObservableProperty<Int>

    fun requestMtu(mtu: Int): DRF<Unit>
    fun read(serviceId: UUID, characteristicId: UUID): DRF<ByteArray>
    fun write(serviceId: UUID, characteristicId: UUID, value: ByteArray): DRF<Unit>
    fun subscribe(
        serviceId: UUID,
        characteristicId: UUID,
        indicate: Boolean = false
    ): Event<ByteArray>
    fun close()
}
