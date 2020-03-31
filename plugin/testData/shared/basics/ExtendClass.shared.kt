package com.test

open class Record {
    var x: Int = 0
    var y: String = ""
    init {
        println("Record created: $x, $y")
    }
    open fun test(){
        println("Test run")
    }
}

class BetterRecord: Record() {
    var z: Double = 0.0
    override fun test() {
        println("Test run ${z.toInt()}")
    }
}

fun main(){
    val record = BetterRecord()
    record.x = 3
    record.y = "Hello"
    record.z = 32.1
    if(record.x == 3){
        record.y = "Set"
    }
    record.test()
    println("x: ${record.x}, y: ${record.y}")
}
