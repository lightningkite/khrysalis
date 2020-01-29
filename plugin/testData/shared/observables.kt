package com.lightningkite.khrysalis.observables.shared

import com.lightningkite.khrysalis.actuals.escaping

fun <Z: AnyObject> captureWeak(capture: Z, lambda: @escaping() (Z) -> Unit): () -> Unit {
    val captured by weak(capture)
    return label@{ ->
        val actualCaptured = captured
        if(actualCaptured == null) {
            return@label
        }
        lambda(actualCaptured!!)
    }
}

fun <Z: AnyObject, A> captureWeak(capture: Z, lambda: @escaping() (Z, A) -> Unit): (A) -> Unit {
    val captured by weak(capture)
    return label@{ a ->
        val actualCaptured = captured
        if(actualCaptured == null) {
            return@label
        }
        lambda(actualCaptured!!, a)
    }
}

abstract class Event<T> {
    abstract fun add(listener: @escaping() (T) -> Boolean): Close
}

abstract class ObservableProperty<T> {
    abstract val value: T
    abstract val onChange: Event<T>
}

abstract class MutableObservableProperty<T>: ObservableProperty<T>() {
    abstract override var value: T
}

typealias Close = ()->Unit

class StandardEvent<T> : Event<T>() {
    val subscriptions: ArrayList<Subscription<T>> = ArrayList<Subscription<T>>()
    var nextIndex: Int = 0

    class Subscription<T>(
        val listener: @escaping() (T) -> Boolean,
        val identifier: Int = 0
    )

    override fun add(listener: @escaping() (T) -> Boolean): Close {
        val thisIdentifier = nextIndex
        nextIndex += 1

        val element = Subscription(
            listener = { item: T ->
                listener(item);
                return@Subscription false
            },
            identifier = thisIdentifier
        )
        subscriptions.add(element)
        return captureWeak(this) { self ->
            self.subscriptions.removeAll { it -> it.identifier == thisIdentifier }
            return@captureWeak Unit
        }
    }

    fun invokeAll(value: T) {
        subscriptions.removeAll { it -> it.listener(value) }
    }

}

class StandardObservableProperty<T>(var underlyingValue: T): MutableObservableProperty<T>() {
    override val onChange: Event<T> = StandardEvent<T>()
    override var value: T
        get() = underlyingValue
        set(value) {
            underlyingValue = value
            onChange.invokeAll(value = value)
        }
}
