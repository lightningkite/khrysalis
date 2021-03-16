package com.lightningkite.khrysalis.context

import kotlin.reflect.KProperty

class ContextProperty<T: Any>() {
    var value: T? = null
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value!!
    }
    inline fun context(value: T, action: ()->Unit){
        if(this.value != null) throw IllegalStateException()
        this.value = value
        action()
        this.value = null
    }
}