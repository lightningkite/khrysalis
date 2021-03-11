@file:SharedCode
package com.test.nulls

import com.lightningkite.butterfly.*

class WebSocketFrame(val binary: ByteArray? = null, val text: String? = null) {
    override fun toString(): String {
        return text ?: binary?.toString(Charsets.UTF_8) ?: "<Empty Frame>"
    }
}

fun main(){
    val frame = WebSocketFrame(text = "asdf")
    val maybeFrame = if(frame.binary != null) frame else null
    maybeFrame?.text?.let { println(it) }
}