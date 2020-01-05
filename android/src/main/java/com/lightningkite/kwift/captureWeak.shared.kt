package com.lightningkite.kwift

import com.lightningkite.kwift.AnyObject
import com.lightningkite.kwift.escaping
import com.lightningkite.kwift.weak


fun <Z : AnyObject> captureWeak(capture: Z, lambda: @escaping() (Z) -> Unit): () -> Unit {
    val captured by weak(capture)
    return label@{ ->
        val actualCaptured = captured
        if (actualCaptured == null) {
            return@label
        }
        lambda(actualCaptured!!)
    }
}

fun <Z : AnyObject, A> captureWeak(capture: Z, lambda: @escaping() (Z, A) -> Unit): (A) -> Unit {
    val captured by weak(capture)
    return label@{ a ->
        val actualCaptured = captured
        if (actualCaptured == null) {
            return@label
        }
        lambda(actualCaptured!!, a)
    }
}

fun <Z : AnyObject, A, B> captureWeak(capture: Z, lambda: @escaping() (Z, A, B) -> Unit): (A, B) -> Unit {
    val captured by weak(capture)
    return label@{ a, b ->
        val actualCaptured = captured
        if (actualCaptured == null) {
            return@label
        }
        lambda(actualCaptured!!, a, b)
    }
}

fun <Z : AnyObject, A, B, C> captureWeak(capture: Z, lambda: @escaping() (Z, A, B, C) -> Unit): (A, B, C) -> Unit {
    val captured by weak(capture)
    return label@{ a, b, c ->
        val actualCaptured = captured
        if (actualCaptured == null) {
            return@label
        }
        lambda(actualCaptured!!, a, b, c)
    }
}

fun <Z : AnyObject, A, B, C, D> captureWeak(
    capture: Z,
    lambda: @escaping() (Z, A, B, C, D) -> Unit
): (A, B, C, D) -> Unit {
    val captured by weak(capture)
    return label@{ a, b, c, d ->
        val actualCaptured = captured
        if (actualCaptured == null) {
            return@label
        }
        lambda(actualCaptured!!, a, b, c, d)
    }
}

fun <Z : AnyObject, A, B, C, D, E> captureWeak(
    capture: Z,
    lambda: @escaping() (Z, A, B, C, D, E) -> Unit
): (A, B, C, D, E) -> Unit {
    val captured by weak(capture)
    return label@{ a, b, c, d, e ->
        val actualCaptured = captured
        if (actualCaptured == null) {
            return@label
        }
        lambda(actualCaptured!!, a, b, c, d, e)
    }
}
