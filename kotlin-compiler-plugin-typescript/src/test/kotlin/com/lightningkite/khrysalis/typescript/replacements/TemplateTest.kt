package com.lightningkite.khrysalis.typescript.replacements

import org.junit.Assert.*
import org.junit.Test

class TemplateTest(){

    @Test
    fun parsing(){
        val result = Template.fromString("~this~.~value~ = ~0~ plus six").parts.joinToString("\n"){ "${it::class.simpleName}: $it" }
        println(result)
        assertEquals("""Receiver: ~this~
Text: .
Value: ~value~
Text:  = 
ParameterByIndex: ~0~
Text:  plus six""", result)
    }
}