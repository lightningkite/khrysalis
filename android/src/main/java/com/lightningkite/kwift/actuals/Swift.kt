package com.lightningkite.kwift.actuals

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty


typealias AnyObject = Any
typealias Equatable = Any

@Target(AnnotationTarget.TYPE)
annotation class escaping

@Target(AnnotationTarget.TYPE)
annotation class swiftExactly(val parameterName: String = "default")

@Target(AnnotationTarget.TYPE)
annotation class swiftDescendsFrom(val parameterName: String = "default")

@Target(AnnotationTarget.FUNCTION)
annotation class discardableResult

class WeakPropertyDelegate<T>(initial: T) {
    var ref = WeakReference<T>(initial)
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return ref.get()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        if (value != null) {
            ref = WeakReference(value)
        }
    }
}

fun <T> weak(value: T) = WeakPropertyDelegate(value)
