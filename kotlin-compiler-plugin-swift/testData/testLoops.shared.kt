package com.test

private fun loopMain(){
    for(item in listOf(1, 2, 3, 4)) {
        println(item)
    }
    for((key, value) in mapOf(1 to 2, 3 to 4).entries){
        println("$key: $value")
    }
    var i = 0
    while(i < 4){
        i++
        println(i)
    }
    label@while(i < 6){
        i++
        println(i)
        break@label
    }
    do {
        i++
        println(i)
    } while(i < 8)
}