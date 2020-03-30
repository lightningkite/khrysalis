package com.test

data class Record(var x: Int, var y: String){
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
    println("x: ${record.x}, y: ${record.y}")
    val copy = record.copy(x = 32)
    println("x: ${copy.x}, y: ${copy.y}")
    if(copy != record){
        println("Not equal")
    }
    record.test()
}
