package com.lightningkite.khrysalis.net

import android.util.Log
import com.lightningkite.khrysalis.PlatformSpecific
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class ConnectedWebSocket(val url: String) : WebSocketListener(),
    Observer<WebSocketFrame> {
    internal var underlyingSocket: WebSocket? = null
    private val _read =
        PublishSubject.create<WebSocketFrame>()
    val ownConnection =
        PublishSubject.create<ConnectedWebSocket>()
    val read: Observable<WebSocketFrame> = HttpClient.run { _read.threadCorrectly() }
    @PlatformSpecific
    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("Socket to $url opened successfully.")
        ownConnection.onNext(this)
    }

    @PlatformSpecific
    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("Socket to $url failed with $t.")
        try {
            ownConnection.onError(t)
            _read.onError(t)
        } catch(e:Exception){
            Log.e("ConnectedWebSocket", "Failed to deliver error")
            e.printStackTrace()
        }
    }

    @PlatformSpecific
    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("Socket to $url closing.")
        ownConnection.onComplete()
        _read.onComplete()
    }

    @PlatformSpecific
    override fun onMessage(webSocket: WebSocket, text: String) {
        println("Socket to $url got message '$text'.")
        _read.onNext(WebSocketFrame(text = text))
    }

    @PlatformSpecific
    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        println("Socket to $url got binary message of length ${bytes.size()}.")
        _read.onNext(WebSocketFrame(binary = bytes.toByteArray()))
    }

    @PlatformSpecific
    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        println("Socket to $url closed.")
    }

    override fun onComplete() {
        underlyingSocket?.close(1000, null)
    }

    @PlatformSpecific
    override fun onSubscribe(d: Disposable) {
    }

    override fun onNext(t: WebSocketFrame) {
        t.text?.let {
            underlyingSocket?.send(it)
        }
        t.binary?.let { binary ->
            underlyingSocket?.send(ByteString.of(binary, 0, binary.size))
        }
    }

    override fun onError(e: Throwable) {
        underlyingSocket?.close(1011, e.message)
    }
}