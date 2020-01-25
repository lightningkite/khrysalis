package com.lightningkite.kwift.bluetooth

import com.lightningkite.kwift.Box
import com.lightningkite.kwift.boxWrap
import com.lightningkite.kwift.observables.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.*

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
