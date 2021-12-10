@file:SharedCode
package com.test.classes

import com.lightningkite.khrysalis.*
import kotlin.math.absoluteValue
import kotlin.random.Random

interface TestInterface {
    val interfaceValue: String get() = "Default"
    fun interfaceFunction(): String = "Default"
}

fun main(){
    val instance = object: TestInterface {
        override val interfaceValue: String
            get() = outsideInfo
    }
    print(instance.interfaceValue)
}