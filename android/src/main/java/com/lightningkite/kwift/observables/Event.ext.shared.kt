package com.lightningkite.kwift.observables

import com.lightningkite.kwift.AnyObject
import com.lightningkite.kwift.discardableResult
import com.lightningkite.kwift.escaping
import com.lightningkite.kwift.weak

@discardableResult
fun <T, A : AnyObject> Event<T>.addWeak(
    referenceA: A,
    listener: @escaping() (A, T) -> Unit
): Close {
    val weakA by weak(referenceA);
    return this.add { item ->
        val a = weakA;
        if (a != null) {
            listener(a, item);
            return@add false
        } else {
            return@add true
        }
    }
}

@discardableResult fun <T, A : AnyObject, B : AnyObject> Event<T>.addWeak(
    referenceA: A,
    referenceB: B,
    listener: @escaping() (A, B, T) -> Unit
): Close {
    val weakA by weak(referenceA)
    val weakB by weak(referenceB)
    return this.add { item ->
        val a = weakA;
        val b = weakB;
        if (a != null && b != null) {
            listener(a, b, item);
            return@add false
        } else {
            return@add true
        }
    }
}

@discardableResult fun <T, A : AnyObject, B : AnyObject, C : AnyObject> Event<T>.addWeak(
    referenceA: A,
    referenceB: B,
    referenceC: C,
    listener: @escaping() (A, B, C, T) -> Unit
): Close {
    val weakA by weak(referenceA)
    val weakB by weak(referenceB)
    val weakC by weak(referenceC)
    return this.add { item ->
        val a = weakA;
        val b = weakB;
        val c = weakC;
        if (a != null && b != null && c != null) {
            listener(a, b, c, item);
            return@add false
        } else {
            return@add true
        }
    }
}


@discardableResult fun <T, A : AnyObject> Event<T>.addAndRunWeak(
    referenceA: A,
    value: T,
    listener: @escaping() (A, T) -> Unit
): Close {
    listener(referenceA, value)
    return addWeak(referenceA, listener)
}

@discardableResult fun <T, A : AnyObject, B : AnyObject> Event<T>.addAndRunWeak(
    referenceA: A,
    referenceB: B,
    value: T,
    listener: @escaping() (A, B, T) -> Unit
): Close {
    listener(referenceA, referenceB, value)
    return addWeak(referenceA, referenceB, listener)
}

@discardableResult fun <T, A : AnyObject, B : AnyObject, C : AnyObject> Event<T>.addAndRunWeak(
    referenceA: A,
    referenceB: B,
    referenceC: C,
    value: T,
    listener: @escaping() (A, B, C, T) -> Unit
): Close {
    listener(referenceA, referenceB, referenceC, value)
    return addWeak(referenceA, referenceB, referenceC, listener)
}
