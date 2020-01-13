package com.lightningkite.kwift.observables

import com.lightningkite.kwift.escaping
import java.io.Closeable

class Close(val closer: @escaping() () -> Unit): Closeable {
    override fun close() {
        closer()
    }
}
