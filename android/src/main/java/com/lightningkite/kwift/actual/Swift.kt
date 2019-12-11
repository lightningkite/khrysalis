package com.lightningkite.kwift.actual

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty


typealias AnyObject = Any
typealias AnyHashable = Any
typealias Equatable = Any
typealias SomeEnum = Enum<*>

@Target(AnnotationTarget.TYPE)
annotation class escaping

@Target(AnnotationTarget.PROPERTY)
annotation class unowned

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class unownedSelf

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class weakSelf

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

fun <T: Any> nullOf(): T? = null

fun <K, V, V2> Map<K, V>.mapValuesToValues(mapper: (V)->V2): Map<K, V2> {
    return this.mapValues { mapper(it.value) }
}
