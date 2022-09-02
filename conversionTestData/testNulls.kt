@file:SharedCode
package com.test.nulls

import com.lightningkite.khrysalis.*
import java.time.*
import java.util.Optional

data class WebSocketFrame(val binary: ByteArray? = null, val text: String? = null) {
    override fun toString(): String {
        return text ?: "<Binary Data>" ?: "<Empty Frame>"
    }
}

inline fun <reified T> test(value: T) {
    println("Hello!")
}

fun main(){
    val frame = WebSocketFrame(text = "asdf")
    val maybeFrame = if(frame.binary != null) frame else null
    maybeFrame?.text?.let { println(it) }

    val o = Optional.of(1)
    test<Optional<Int>>(Optional.of(1))
    test(Optional.of(1))
    println(o.isPresent)
    println(o.get())

//    val platformNullability = Instant.now()
//    platformNullability.atZone(ZoneId.systemDefault())
}