package com.lightningkite.kwift.observables

import com.lightningkite.kwift.escaping

class WorkEvent<T>(val work: @escaping() () -> T) : EnablingEvent<T>() {
    var result: T? = null
    var complete: Boolean = false
    override fun postEnable() {
        if (!complete) {
            result = work()
            complete = true
        }
        invokeAll(result as T)
    }
}
