package com.lightningkite.khrysalis

import android.net.Uri
import java.lang.ref.WeakReference
import kotlin.reflect.KProperty


typealias AnyObject = Any
typealias AnyHashable = Any
typealias Hashable = Any
typealias Equatable = Any
typealias SomeEnum = Enum<*>
typealias Uri = Uri

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

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.EXPRESSION)
@Retention(AnnotationRetention.SOURCE)
annotation class swiftReturnType(val text: String)

@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPE
)
annotation class PlatformSpecific

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
