package com.lightningkite.khrysalis

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty


typealias AnyObject = Any
typealias AnyEquatable = Any?
typealias AnyHashable = Any?
typealias IsEquatable = Any?
typealias IsHashable = Any?
interface Equatable {}
interface Hashable: Equatable {}
typealias SomeEnum = Enum<*>

interface Codable
typealias IsCodable = Any?
typealias IsCodableAndHashable = Any?
typealias IsCodableAndEquatable = Any?
typealias UntypedList = List<*>
typealias UntypedMap = Map<*, *>

@Target(AnnotationTarget.FILE)
annotation class SharedCode

@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.TYPE,
    AnnotationTarget.FILE,
    AnnotationTarget.TYPEALIAS
)
annotation class JsName(val name: String)

@Target(AnnotationTarget.TYPE)
annotation class Escaping

@Target(AnnotationTarget.CLASS)
annotation class SwiftMustBeClass

@Target(AnnotationTarget.PROPERTY)
annotation class Unowned

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Modifies

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class SwiftName(val name: String)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class UnownedSelf

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class WeakSelf

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class CaptureUnowned(vararg val keys: String)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class CaptureWeak(vararg val keys: String)

@Target(AnnotationTarget.TYPE)
annotation class SwiftExactly(val parameterName: String = "default")

@Target(AnnotationTarget.TYPE)
annotation class SwiftDescendsFrom(val parameterName: String = "default")

@Target(AnnotationTarget.FUNCTION)
annotation class DiscardableResult

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.EXPRESSION)
@Retention(AnnotationRetention.SOURCE)
annotation class SwiftReturnType(val text: String)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class SwiftExtensionWhere(val text: String)

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

fun fatalError(reason: String = ""): Nothing {
    throw Error(reason)
}