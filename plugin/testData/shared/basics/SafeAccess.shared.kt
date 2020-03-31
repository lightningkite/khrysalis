package com.test

data class SillyBox(var subBox: SillyBox? = null)

fun main(){
    val item = SillyBox(SillyBox())
    item.subBox?.subBox?.let {
        println("I got a box!")
    } ?: run {
        println("I didn't get a box...")
    }

    item.subBox?.subBox?.subBox?.subBox = SillyBox()
    item.subBox?.subBox?.let {
        println("I got a box!")
    } ?: run {
        println("I didn't get a box...")
    }

    item.subBox?.subBox = SillyBox()
    item.subBox?.subBox?.let {
        println("I got a box!")
    } ?: run {
        println("I didn't get a box...")
    }
}
