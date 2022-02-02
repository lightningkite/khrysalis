@file:SharedCode
package com.test.replacements

import com.lightningkite.khrysalis.*

interface HasProperty {
    val value: Int
}
class ConcreteProperty(override val value: Int): HasProperty

fun main(){
    val prop1 = ConcreteProperty::value
    val prop2 = HasProperty::value
}
