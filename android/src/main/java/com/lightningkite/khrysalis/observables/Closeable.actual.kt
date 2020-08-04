package com.lightningkite.khrysalis.observables

import com.lightningkite.khrysalis.Escaping
import io.reactivex.disposables.Disposable

interface Closeable: Disposable {
    override fun isDisposed(): Boolean {
        return false
    }

    override fun dispose() {
        close()
    }

    fun close()
}

class Close(val closer: @Escaping() () -> Unit): Closeable {
    var disposed: Boolean = false
    override fun isDisposed(): Boolean {
        return disposed
    }

    override fun close() {
        disposed = true
        closer()
    }
}
