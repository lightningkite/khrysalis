@file:SharedCode
package com.test.exceptions

import com.lightningkite.khrysalis.*

@Throws(IllegalStateException::class, IllegalArgumentException::class)
fun failableAction(state: Boolean = true) {
    if (state) {
        throw IllegalStateException("Reasons")
    } else {
        throw IllegalArgumentException("I won't do that for you.  You said no to state.")
    }
}

fun hasFailableLambda(action: (@Throws(IllegalArgumentException::class) ()->Unit)) {
    try {
        action()
        action.invoke()
    } catch (e: Throwable) {
        println(e.message ?: "-")
    }
}

fun ignoreMe() {
    fatalError()
    fatalError("Message")
}

fun main() {
    var testValue = 0
    try {
        failableAction()
    } catch (e: Throwable) {
        println(e.message ?: "-")
    }

    try {
        failableAction()
    } catch (e: IllegalStateException) {
        println("IllegalStateException was thrown")
    } catch (e: IllegalArgumentException) {
        println("IllegalArgumentException was thrown")
    } catch (e: Throwable) {
        println("Something else was thrown")
    }

    val value = try {
        failableAction()
        true
    } catch (e: Throwable) {
        println(e.message ?: "-")
        false
    }
    val value2 = try {
        failableAction()
    } catch (e: Throwable) {
        null
    }

    hasFailableLambda {
        if(testValue > 0) {
            throw IllegalStateException()
        }
        println("Hello!")
    }

}