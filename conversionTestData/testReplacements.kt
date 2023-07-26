@file:SharedCode
package com.test.replacements

import com.lightningkite.khrysalis.*
import java.security.MessageDigest

interface HasProperty {
    val value: Int
}
class ConcreteProperty(override val value: Int): HasProperty

fun main(){
    val prop1 = ConcreteProperty::value
    val prop2 = HasProperty::value
    println("Test".sha1())
}
