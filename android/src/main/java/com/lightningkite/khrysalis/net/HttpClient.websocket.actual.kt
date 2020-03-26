package com.lightningkite.khrysalis.net

import com.lightningkite.khrysalis.bytes.Data
import com.lightningkite.khrysalis.net.HttpClient.threadCorrectly
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import okhttp3.*
import okio.ByteString
import java.util.*


fun HttpClient.webSocket(
    url: String,
    headers: Map<String, String> = mapOf()
): Single<ConnectedWebSocket> = Single.create<ConnectedWebSocket> { emitter ->
    val out = PublishSubject.create<WebSocketFrame>()
    var external: ConnectedWebSocketImpl? = null
    val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            println("Socket to $url opened successfully.")
            if (external == null) {
                external = ConnectedWebSocketImpl(webSocket, out.threadCorrectly())
                emitter.onSuccess(external!!)
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            println("Socket to $url failed with $t.")
            val external = external
            if (external == null) {
                emitter.onError(t)
            } else {
                out.onError(t)
                external.dispose()
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            println("Socket to $url closing.")
            external?.let {
                out.onComplete()
                it.dispose()
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            println("Socket to $url got message '$text'.")
            out.onNext(WebSocketFrame(text = text))
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            println("Socket to $url got binary message of length ${bytes.size()}.")
            out.onNext(WebSocketFrame(binary = bytes.toByteArray()))
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            if (external == null) {
                emitter.onError(Exception("Socket closed before release."))
            }
            println("Socket to $url closed.")
        }
    }
    client.newWebSocket(
        Request.Builder()
            .url(url.replace("http", "ws"))
            .headers(Headers.of(headers))
            .addHeader("Accept-Language", Locale.getDefault().language)
            .build(),
        listener
    )
}.threadCorrectly()

private class ConnectedWebSocketImpl(val socket: WebSocket, override val read: Observable<WebSocketFrame>) :
    ConnectedWebSocket {
    override fun writeBinary(binary: Data) {
        println("Socket sent binary data of length ${binary.size}")
        socket.send(ByteString.of(binary, 0, binary.size))
    }

    override fun writeText(text: String) {
        println("Socket sent text '$text'.")
        socket.send(text)
    }

    var closed: Boolean = false
    override fun isDisposed(): Boolean {
        return closed
    }

    override fun dispose() {
        println("Socket requested disconnect locally.")
        socket.close(1000, null)
        closed = true
    }

}

class WebSocketFrame(val binary: Data? = null, val text: String? = null) {
    override fun toString(): String {
        return text ?: binary?.toString(Charsets.UTF_8) ?: "<Empty Frame>"
    }
}
