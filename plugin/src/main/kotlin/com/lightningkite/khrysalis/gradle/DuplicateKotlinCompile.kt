package com.lightningkite.khrysalis.gradle

fun <T: Any> unsafeDuplicate(obj: T, initialCreate: ()->T): T {
    val duplicate = initialCreate()
    for(field in obj::class.java.fields) {
        field.isAccessible = true
        field.set(duplicate, field.get(obj))
    }
    return duplicate
}
