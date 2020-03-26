package com.lightningkite.khrysalis.net

import com.lightningkite.khrysalis.bytes.Data
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable


interface ConnectedWebSocket: Observer<WebSocketFrame> {
    val read: Observable<WebSocketFrame>
    val ownConnection: Observable<ConnectedWebSocket>
}
