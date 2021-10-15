package com.lightningkite.butterfly.net

import com.lightningkite.butterfly.Codable
import com.lightningkite.butterfly.fromJsonString
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.*
import org.junit.Test

class HttpClientTest {
    data class Post(val userId: Long, val id: Long, val title: String, val body: String): Codable
//    @Test fun testCall(){
//        HttpClient.ioScheduler = null
//        HttpClient.responseScheduler = null
//        HttpClient.call("https://jsonplaceholder.typicode.com/posts/")
//            .readJson<List<Post>>()
//            .blockingGet()
//            .let { println("postsIntended: ${it[0]}") }
//
//    }
//
//    @Test fun testWebSocket(){
//        HttpClient.ioScheduler = null
//        HttpClient.responseScheduler = null
//        println("Connecting...")
//        var recievedFrame: Boolean = false
//        var mySocket: ConnectedWebSocket? = null
//        HttpClient.webSocket("wss://echo.websocket.org").subscribeOn(Schedulers.io()).subscribe { socket ->
//            mySocket = socket
//            socket.read.subscribeOn(Schedulers.io()).subscribe { frame ->
//                println("Frame: $frame")
//                recievedFrame = true
//            }
//            socket.onNext(WebSocketFrame(text = "Hello world!"))
//        }
//        Thread.sleep(3000L)
//        mySocket?.onComplete()
//        assert(recievedFrame)
//    }
}
