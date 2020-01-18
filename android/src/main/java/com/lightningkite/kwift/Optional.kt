package com.lightningkite.kwift

sealed class Optional<out T> {
    val value: T? get() = (this as? OptionalSome<T>)?.element
    val valueNN: T get() = (this as OptionalSome<T>).element

    companion object {
        fun <T> some(value: T) = OptionalSome(value)
        fun <T> wrap(value: T): Optional<T> {
            if(value == null) return OptionalNone
            else return OptionalSome(value)
        }

        val none = OptionalNone
    }
}

class OptionalSome<out T>(val element: T): Optional<T>()
object OptionalNone: Optional<Nothing>()

//object NullRep
//inline class Optional<out T>(val uglyValue: Any?) {
//    @Suppress("UNCHECKED_CAST")
//    val value: T? get() = if(uglyValue === NullRep) null else uglyValue as T
//    val valueNN: T get() = if(uglyValue === NullRep) throw IllegalArgumentException() else uglyValue as T
//    companion object {
//        fun <T> some(value: T) = Optional<T>(value)
//        fun <T> wrap(value: T): Optional<T> {
//            if(value == null) return Optional(NullRep)
//            else return Optional(value)
//        }
//
//        val none = NullRep
//    }
//}
