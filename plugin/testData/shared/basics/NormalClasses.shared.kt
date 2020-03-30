package com.test

class Record(var x: Int, var y: String) {
    init {
        println("Record created: $x, $y")
    }
    fun test(){
        println("Test run")
    }
}

fun main(){
    val record = Record(x = 3, y = "Hello")
    if(record.x == 3){
        record.y = "Set"
    }
    record.test()
    println("x: ${record.x}, y: ${record.y}")
}
