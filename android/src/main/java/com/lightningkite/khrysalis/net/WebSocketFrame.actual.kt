package com.lightningkite.khrysalis.net

import com.lightningkite.khrysalis.bytes.Data
import io.reactivex.Observable
import okhttp3.Headers
import okhttp3.Request
import java.util.*

class WebSocketFrame(val binary: Data? = null, val text: String? = null) {
    override fun toString(): String {
        return text ?: binary?.let { "<Binary data length ${it.size}" } ?: "<Empty Frame>"
    }
}
