package com.lightningkite.kwift.observables.shared

import com.lightningkite.kwift.actual.AnyObject

class ObservableStack<T : AnyObject> : ObservableProperty<List<T>>() {

    companion object {
        fun <T: AnyObject> withFirst(value: T): ObservableStack<T> {
            val result = ObservableStack<T>()
            result.reset(value)
            return result
        }
    }

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

    fun swap(t: T) {
        stack.removeAt(stack.lastIndex)
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

    fun dismiss(): Boolean {
        if (stack.isEmpty()) {
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
