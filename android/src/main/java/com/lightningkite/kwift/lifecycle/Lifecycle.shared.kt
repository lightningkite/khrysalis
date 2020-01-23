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
    override val onChange: Observable<Box<Boolean>>
        get() = basedOn.onChange.take(1)

}

fun ObservableProperty<@swiftExactly Boolean>.closeWhenOff(closeable: Disposable) {
    var listener: Disposable? = null
    listener = this.observableNN.subscribe { it ->
        if(!it) {
            closeable.dispose()
            listener?.dispose()
        }
    }
}

val appInForeground = StandardObservableProperty(false)
