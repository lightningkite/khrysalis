@file:SharedCode
package com.test.loops

import com.lightningkite.butterfly.*

fun main(){
    for(item in listOf(1, 2, 3, 4)) {
        println(item)
    }
    val map = mapOf(1 to 2, 3 to 4)
    for((key, value) in map.entries.sortedBy { it.key }){
        println("$key: $value")
    }
    var i = 0
    while(i < 4){
        i++
        println(i)
    }
    label@while(i < 6){
        i++
        println(i)
        break@label
    }
    do {
        i++
        println(i)
    } while(i < 8)
}