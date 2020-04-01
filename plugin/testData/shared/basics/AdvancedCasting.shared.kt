package com.test

interface AdvancedCastingTestInterface {}
class AdvancedCastingTestImplementation: AdvancedCastingTestInterface {}

fun main(){
    var value: Any? = null
    val asString = value as? String
    val asTestInterface = value as? AdvancedCastingTestInterface
    val asTestImplementation = value as? AdvancedCastingTestImplementation
}
