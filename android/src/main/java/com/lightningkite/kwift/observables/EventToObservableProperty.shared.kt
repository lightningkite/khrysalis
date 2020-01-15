package com.lightningkite.kwift.observables

import com.lightningkite.kwift.escaping
import com.lightningkite.kwift.weak

class EventToObservableProperty<T>(override var value: T, val wrapped: Event<T>): ObservableProperty<T>() {

    class MyEvent<T>(): EnablingEvent<T>() {
        var parent: EventToObservableProperty<T>? = null
        var sub: Close? = null

        override fun enable() {
            val weakThis by weak(this)
            sub = parent?.wrapped?.add { it ->
                weakThis?.parent?.value = it
                return@add weakThis != null
            }
        }

        override fun disable() {
            sub?.close()
        }
    }

    override val onChange: Event<T> get() = backingEvent
    val backingEvent: MyEvent<T> = MyEvent()

    init {
        backingEvent.parent = this
    }
}

fun <T> Event<T>.asObservableProperty(defaultValue: T): ObservableProperty<T> {
    return EventToObservableProperty<T>(defaultValue, this)
}

