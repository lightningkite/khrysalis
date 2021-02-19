@file:SharedCode
package com.test.exceptions

import com.lightningkite.butterfly.*
fun failableAction(state: Boolean = true) {
    if (state) {
        throw IllegalStateException("Reasons")
    } else {
        throw IllegalArgumentException("I won't do that for you.  You said no to state.")
    }
}

fun main() {
    try {
        failableAction()
    } catch (e: Exception) {
        println(e.message)
    }

    try {
        failableAction()
    } catch (e: IllegalStateException) {
        println("IllegalStateException was thrown")
    } catch (e: IllegalArgumentException) {
        println("IllegalArgumentException was thrown")
    } catch (e: Exception) {
        println("Something else was thrown")
    }

    val value = try {
        failableAction()
        true
    } catch (e: Exception) {
        println(e.message)
        false
    }
}