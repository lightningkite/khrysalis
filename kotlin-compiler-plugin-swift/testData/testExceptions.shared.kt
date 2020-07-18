package com.test

@Throws(IllegalStateException::class, IllegalArgumentException::class)
private fun failableAction(state: Boolean = true): Int {
    if (state) {
        throw IllegalStateException("Reasons")
    } else {
        throw IllegalArgumentException("I won't do that for you.  You said no to state.")
    }
}

private fun exceptionTest() {
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
    val value2 = try {
        failableAction()
    } catch (e: Exception) {
        null
    }

    val frickYou = failableAction(true)
}