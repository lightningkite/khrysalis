package com.lightningkite.kwift.observables

import java.io.Closeable
import com.lightningkite.kwift.escaping

typealias Closeable = Closeable

class Close(val closer: @escaping() () -> Unit): Closeable {
    override fun close() {
        closer()
    }
}
