package com.lightningkite.khrysalis.net

import com.lightningkite.khrysalis.Codable
import com.lightningkite.khrysalis.fromJsonString
import io.reactivex.Single
import org.junit.Assert.*
import org.junit.Test

class HttpClientTest {
    data class Post(val userId: Long, val id: Long, val title: String, val body: String): Codable
    @Test fun testCall(){
        HttpClient.ioScheduler = null
        HttpClient.responseScheduler = null
        val postsManual = HttpClient.call("https://jsonplaceholder.typicode.com/posts/")
            .blockingGet()
            .readJson<List<Post>>()
            .let { println("postsManual: ${it[0]}") }
        HttpClient.call("https://jsonplaceholder.typicode.com/posts/")
            .map { it -> it.readJson<List<Post>>() }
            .blockingGet()
            .let { println("postsManualAlt: ${it[0]}") }
        HttpClient.call("https://jsonplaceholder.typicode.com/posts/")
            .readJson<List<Post>>()
            .blockingGet()
            .let { println("postsIntended: ${it[0]}") }

    }
}
