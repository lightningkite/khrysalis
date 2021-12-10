@file:SharedCode
package com.test.nulls

import com.lightningkite.khrysalis.*
import java.time.*

data class WebSocketFrame(val binary: ByteArray? = null, val text: String? = null) {
    override fun toString(): String {
        return text ?: "<Binary Data>" ?: "<Empty Frame>"
    }
}

fun main(){
    val frame = WebSocketFrame(text = "asdf")
    val maybeFrame = if(frame.binary != null) frame else null
    maybeFrame?.text?.let { println(it) }

//    val platformNullability = Instant.now()
//    platformNullability.atZone(ZoneId.systemDefault())
}