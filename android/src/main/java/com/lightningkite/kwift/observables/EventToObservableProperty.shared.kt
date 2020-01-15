package com.lightningkite.kwift.observables

import com.lightningkite.kwift.escaping

class EventToObservableProperty<T>(override var value: T, val wrapped: Event<T>): ObservableProperty<T>() {

    class MyEvent<T>(val self: EventToObservableProperty<T>): EnablingEvent<T>() {

        var sub: Close? = null

        override fun enable() {
            sub = self.wrapped.add { it ->
                self.value = it
                return@add false
            }
        }

        override fun disable() {
            sub?.close()
        }
    }

    override val onChange: Event<T> = MyEvent(this)
}

fun <T> Event<T>.asObservableProperty(defaultValue: T): ObservableProperty<T> {
    return EventToObservableProperty<T>(defaultValue, this)
}

