@file:SharedCode
package com.test.genericprotocol

import com.lightningkite.khrysalis.*

interface Test<ID> {
    val test: ID
}

class Impl: Test<Int> {
    override val test: Int
        get() = 2
}

fun main(){
    println(Impl().test)
}