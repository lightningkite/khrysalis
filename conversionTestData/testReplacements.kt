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

fun String.sha1(): String {
    val data = MessageDigest.getInstance("SHA-1").digest(this.toByteArray(Charsets.UTF_8))
    val builder = StringBuilder()
    for(byte in data) {
        builder.append((byte.toInt() and 0xFF).toString(16).padStart(2, '0'))
    }
    return builder.toString()
}