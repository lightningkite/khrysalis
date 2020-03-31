package com.test

data class Record(var x: Int, var y: String){
    init {
        println("Record created: $x, $y")
    }
    fun test(){
        println("Test run")
    }

    companion object {
        val theMeaning = Record(42, "The Question")
        fun make(x: Int, y: String): Record = Record(x, y)
    }
}

fun main(){
    Record.theMeaning.test()
    Record.make(43, "One more").test()
}
