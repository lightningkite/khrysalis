package com.lightningkite.kwift.observables

import com.lightningkite.kwift.escaping
import com.lightningkite.kwift.captureWeak

abstract class EnablingEvent<T> : Event<T>() {

    abstract fun enable()
    abstract fun disable()

    val subscriptions: ArrayList<Subscription<T>> = ArrayList()
    var nextIndex: Int = 0

    class Subscription<T>(
        val listener: @escaping() (T) -> Boolean,
        val identifier: Int = 0
    )

    override fun add(listener: @escaping() (T) -> Boolean): Close {
        if(subscriptions.isNotEmpty()){
            enable()
        }

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
        return Close(captureWeak(this) { self ->
            self.subscriptions.removeAll { it -> it.identifier == thisIdentifier }
            if(self.subscriptions.isEmpty()){
                self.disable()
            }
            return@captureWeak Unit
        })
    }

    fun invokeAll(value: T) {
        subscriptions.removeAll { it -> it.listener(value) }
    }

}
