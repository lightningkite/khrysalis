package com.test.nulls

class WebSocketFrame(val binary: ByteArray? = null, val text: String? = null) {
    override fun toString(): String {
        return text ?: binary?.toString(Charsets.UTF_8) ?: "<Empty Frame>"
    }
}

private fun test(){
    val frame = WebSocketFrame(text = "asdf")
    val maybeFrame = if(frame.binary != null) frame else null
    maybeFrame?.text?.let { println(it) }
}