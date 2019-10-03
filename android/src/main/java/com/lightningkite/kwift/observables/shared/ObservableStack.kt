package com.lightningkite.kwift.observables.shared

import com.lightningkite.kwift.actuals.AnyObject

class ObservableStack<T : AnyObject> : ObservableProperty<List<T>>() {
    override val onChange: StandardEvent<List<T>> = StandardEvent<List<T>>()
    override val value: List<T>
        get() {
            return stack
        }

    val stack: ArrayList<T> = ArrayList<T>()

    fun push(t: T) {
        stack.add(t)
        onChange.invokeAll(value = stack)
    }

    fun pop(): Boolean {
        if (stack.size <= 1) {
            return false
        }
        stack.removeAt(stack.lastIndex)
        onChange.invokeAll(value = stack)
        return true
    }

    fun popTo(t: T) {
        var found = false
        for (i in 0..stack.lastIndex) {
            if (found) {
                stack.removeAt(stack.lastIndex)
            } else if (stack[i] === t) {
                found = true
            }
        }
        onChange.invokeAll(value = stack)
    }

    fun root() {
        popTo(t = stack.first())
    }

    fun reset(t: T) {
        stack.clear()
        stack.add(t)
        onChange.invokeAll(value = stack)
    }
}
