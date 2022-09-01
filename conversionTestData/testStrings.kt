@file:SharedCode
package com.test.strings

import com.lightningkite.khrysalis.*
fun main(){
    val char = '\u001f'
    val string = "test\u001f"
    println(string.substringBefore("st"))
}