package com.lightningkite.kwift.observables

import com.lightningkite.kwift.escaping
import com.lightningkite.kwift.captureWeak
import com.lightningkite.kwift.post

class ForceMainThreadEvent<T> : InvokableEvent<T>() {
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
        post {
            this.subscriptions.add(element)
        }
        return Close(captureWeak(this) { self ->
            post {
                self.subscriptions.removeAll { it -> it.identifier == thisIdentifier }
            }
            return@captureWeak Unit
        })
    }

    override fun invokeAll(value: T) {
        post {
            this.subscriptions.removeAll { it -> it.listener(value) }
        }
    }

}
