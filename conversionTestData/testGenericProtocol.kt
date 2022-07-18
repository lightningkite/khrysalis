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

//fun <T: Test<*>> T.a() {}
//fun <T: Test<Int>> T.b() {}
//fun <T: Test<ID>, ID: Comparable<ID>> T.c() {}
//fun <T: Test<*>> d(item: T) {}
//fun <T: Test<Int>> e(item: T) {}
//fun <T: Test<ID>, ID: Comparable<ID>> f(item: T, id: ID) {}
//fun <T: Test<ID>, ID> g(item: T, id: ID) {}
//
//class TestA<T: Test<Int>>(val t: T)
//class TestB<T: Test<ID>, ID>(val t: T, val id: ID)

fun main(){
    println(Impl().test)
//    Impl().a()
//    Impl().b()
//    Impl().c()
//    TestA(Impl())
//    TestB(Impl(), 1)
}