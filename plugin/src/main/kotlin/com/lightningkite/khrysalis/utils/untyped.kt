package com.lightningkite.khrysalis.utils

class UntypedThing(val wraps: Any?) {
    operator fun get(key: String): UntypedThing {
        if(wraps == null) return this
        try {
            return UntypedThing(wraps::class.java.getField(key))
        } catch(e:Exception) {
            return UntypedThing(null)
        }
    }
    fun options() = if(wraps != null) wraps::class.java.fields.map { it.name } else listOf()
    inline fun <reified T: Any> asType(): T? = wraps as? T
}
val Any?.untyped get() = UntypedThing(this)
