@file:SharedCode
package com.test.annot

import kotlin.math.absoluteValue
import com.lightningkite.khrysalis.*

@Throws(IllegalArgumentException::class) fun test(){}

val lambda: @Escaping() ()->Unit = {}

fun main(){
    lambda()
    println("Good!")
}