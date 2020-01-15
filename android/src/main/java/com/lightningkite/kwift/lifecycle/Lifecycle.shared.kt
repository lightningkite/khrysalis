package com.lightningkite.kwift.lifecycle

import com.lightningkite.kwift.escaping
import com.lightningkite.kwift.observables.*
import com.lightningkite.kwift.swiftExactly
import com.lightningkite.kwift.weak

typealias Lifecycle = ObservableProperty<Boolean>

infix fun ObservableProperty<@swiftExactly Boolean>.and(other: ObservableProperty<@swiftExactly Boolean>): Lifecycle = this.combine(other) { a, b -> a && b }
fun <A: Any> ObservableProperty<@swiftExactly Boolean>.openCloseBinding(
    target: A,
    open: @escaping() (A)->Unit,
    close: @escaping() (A)->Unit
) {
    var lastValue = this.value
    if(this.value){
        open(target)
    }
    this.addAndRunWeak(target) { target, value ->
        if(lastValue && !value) {
            close(target)
        }
        if(!lastValue && value){
            open(target)
        }
        lastValue = value
    }
}

fun ObservableProperty<@swiftExactly Boolean>.once(): ObservableProperty<Boolean> = OnceObservableProperty(this)

private class OnceObservableProperty(val basedOn: ObservableProperty<Boolean>): ObservableProperty<Boolean>() {
    override val value: Boolean
        get() = basedOn.value
    override val onChange: Event<Boolean>
        get() = OnceEvent(basedOn.onChange)

    class OnceEvent(val basedOn: Event<Boolean>) : Event<Boolean>() {
        override fun add(listener: (Boolean) -> Boolean): Close {
            return basedOn.add { it ->
                val result = listener(it)
                return@add result && !it
            }
        }
    }

}

fun ObservableProperty<@swiftExactly Boolean>.closeWhenOff(closeable: Closeable) {
    val weakCloseable by weak(closeable)
    this.onChange.add { it ->
        weakCloseable?.let { closeable ->
            if(!it) {
                closeable.close()
                return@add true
            }
            return@add false
        } ?: run {
            return@add true
        }
    }
}

val appInForeground = StandardObservableProperty(false)
