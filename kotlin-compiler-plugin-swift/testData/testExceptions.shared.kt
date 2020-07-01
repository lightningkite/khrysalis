package com.test.exceptions

fun failableAction(state: Boolean = true) {
    if (state) {
        throw IllegalStateException("Reasons")
    } else {
        throw IllegalArgumentException("I won't do that for you.  You said no to state.")
    }
}

fun test() {
    try {
        failableAction()
    } catch (e: Exception) {
        e.printStackTrace()
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
        e.printStackTrace()
        false
    }
}