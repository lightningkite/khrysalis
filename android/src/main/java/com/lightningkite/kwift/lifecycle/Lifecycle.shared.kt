package com.lightningkite.kwift.lifecycle

import com.lightningkite.kwift.*
import com.lightningkite.kwift.observables.*
import com.lightningkite.kwift.rx.add
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

typealias Lifecycle = ObservableProperty<Boolean>

infix fun ObservableProperty<@swiftExactly Boolean>.and(other: ObservableProperty<@swiftExactly Boolean>): Lifecycle = this.combine(other) { a, b -> a && b }
fun <A: AnyObject> ObservableProperty<@swiftExactly Boolean>.openCloseBinding(
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
    override val onChange: Observable<Optional<Boolean>>
        get() = basedOn.onChange.take(1)

}

fun <T> ObservableProperty<@swiftExactly Boolean>.closeWhenOff(closeable: T) where T: Disposable, T: AnyObject {
    val weakCloseable by weak(closeable)
    this.onChange.add { it ->
        weakCloseable?.let { closeable ->
            if(!(it.value as Boolean)) {
                closeable.dispose()
                return@add true
            }
            return@add false
        } ?: run {
            return@add true
        }
    }
}

val appInForeground = StandardObservableProperty(false)
