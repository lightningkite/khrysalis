@file:SharedCode
package com.test.selfreference

import com.lightningkite.khrysalis.*

class TestClass(val x: Int) {
    val y = run {
        x + 2
    }
    val z: Int? = run {
        if(y > 2)
            y
        else
            null
    }
}

fun main(){
}