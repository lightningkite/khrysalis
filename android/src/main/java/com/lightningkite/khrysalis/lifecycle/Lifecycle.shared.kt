package com.lightningkite.khrysalis.lifecycle

import com.lightningkite.khrysalis.*
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.add
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

@Deprecated("Just use RX disposal stuff")
typealias Lifecycle = ObservableProperty<Boolean>

@Deprecated("Just use RX disposal stuff")
infix fun ObservableProperty<@swiftExactly Boolean>.and(other: ObservableProperty<@swiftExactly Boolean>): Lifecycle = this.combine(other) { a, b -> a && b }
@Deprecated("Just use RX disposal stuff")
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
fun ObservableProperty<@swiftExactly Boolean>.openCloseBinding(
    open: @escaping() ()->Unit,
    close: @escaping() ()->Unit
) {
    var lastValue = this.value
    if(this.value){
        open()
    }
    val everlasting = this.observableNN.subscribeBy { value ->
        if(lastValue && !value) {
            close()
        }
        if(!lastValue && value){
            open()
        }
        lastValue = value
    }
}

@Deprecated("Just use RX disposal stuff")
fun ObservableProperty<@swiftExactly Boolean>.once(): ObservableProperty<Boolean> = OnceObservableProperty(this)

@Deprecated("Just use RX disposal stuff")
private class OnceObservableProperty(val basedOn: ObservableProperty<Boolean>): ObservableProperty<Boolean>() {
    override val value: Boolean
        get() = basedOn.value
    override val onChange: Observable<Box<Boolean>>
        get() = basedOn.onChange.take(1)

}

@Deprecated("Just use RX disposal stuff")
fun ObservableProperty<@swiftExactly Boolean>.closeWhenOff(closeable: Disposable) {
    var listener: Disposable? = null
    listener = this.observableNN.subscribe { it ->
        if(!it) {
            closeable.dispose()
            listener?.dispose()
        }
    }
}

@Deprecated("Just use RX disposal stuff")
val appInForeground = StandardObservableProperty(false)
