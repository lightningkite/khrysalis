package com.lightningkite.kwift.observables

import com.lightningkite.kwift.captureWeak
import com.lightningkite.kwift.escaping

class OnceEvent<T> : InvokableEvent<T>() {
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
        return Close(captureWeak(this) { self ->
            self.subscriptions.removeAll { it -> it.identifier == thisIdentifier }
            return@captureWeak Unit
        })
    }

    var hasBeenInvoked: Boolean = false
    override fun invokeAll(value: T) {
        if (hasBeenInvoked) return
        subscriptions.removeAll { it -> it.listener(value) }
        hasBeenInvoked = true
    }

}

