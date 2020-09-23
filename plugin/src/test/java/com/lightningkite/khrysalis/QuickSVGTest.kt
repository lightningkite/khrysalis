package com.lightningkite.khrysalis

import org.junit.Test

class QuickSVGTest {
    @Test fun quick(){
        val exampleData = "-1,2 3-4"
        val expected = listOf(-1.0, 2.0, 3.0, -4.0)
        val arguments = ArrayList<Double>()
        val currentNumber = StringBuilder()
        for(c in exampleData){
            when(c){
                ' ', ',', '-' -> {
                    if(currentNumber.length > 0){
                        arguments.add(currentNumber.toString().toDouble())
                        currentNumber.setLength(0)
                    }
                    if(c == '-'){
                        currentNumber.append('-')
                    }
                }
                in '0'..'9', '.' -> currentNumber.append(c)
            }
        }
        if(currentNumber.length > 0){
            arguments.add(currentNumber.toString().toDouble())
        }
        println("Expected: $expected")
        println("Actual: $arguments")
        for(index in expected.indices){
            assert(expected[index] == arguments[index])
        }
    }
}