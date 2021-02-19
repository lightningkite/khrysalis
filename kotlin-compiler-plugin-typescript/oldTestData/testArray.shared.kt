package com.test.arrays

fun testArrays(){
    val myArray = doubleArrayOf(1.0, 2.0, 3.0)
    fun sum(array: DoubleArray): Double{
        var total = 0.0
        for(i in 0 until myArray.size) {
            total += myArray[i]
        }
        return total
    }
    sum(myArray)
}
