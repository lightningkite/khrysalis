@file:SharedCode
package com.test.identifiers

import com.lightningkite.khrysalis.*

fun delete() {}
class Thing() {
    fun delete() {}
}

fun main() {
    delete()
    Thing().delete()
}