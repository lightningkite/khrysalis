@file:SharedCode
package com.test.booleans

import com.lightningkite.khrysalis.*
fun main(){
    val t = 1 > 0
    val f = 0 > 1

    println(t)
    println(f)
    println(t && f)
    println(f && t)
    println(t || f)
    println(f || t)
    println(t xor f)
    println(f xor t)
    println(!t)
    println(!f)
}