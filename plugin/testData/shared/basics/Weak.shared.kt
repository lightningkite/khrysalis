package com.test

import com.lightningkite.khrysalis.*

class SillyBox(val x: Int)

class Stuff(box: SillyBox){
    val weakBox: SillyBox? by weak(box)
}

fun main(){
    val sillyBox = SillyBox(3)
    val stuff = Stuff(sillyBox)
    println(stuff.weakBox?.x?.toString() ?: "-")
}
