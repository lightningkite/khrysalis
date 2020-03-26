package com.lightningkite.khrysalis.net

import com.lightningkite.khrysalis.bytes.Data
import io.reactivex.Observable
import io.reactivex.disposables.Disposable


interface ConnectedWebSocket: Disposable {
    fun writeBinary(binary: Data)
    fun writeText(text: String)
    val read: Observable<WebSocketFrame>
}
