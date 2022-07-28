@file:SharedCode
package com.test.objects

import com.lightningkite.khrysalis.*
import kotlin.random.*

interface TestInterface {
    fun test()
}

sealed class Test<T>: TestInterface {
    abstract override fun test()
    object A: Test<Int>() {
        override fun test() { println("A") }
    }
    class B<T>(val x: T): Test<T>() {
        override fun test() { println("B $x") }
    }
    companion object {
        fun testInsideInterface() {
            listOf<Test<Int>>(Test.A, Test.B(3)).forEach {
                it.test()
            }
            listOf<TestInterface>(Test.A, Test.B(3)).forEach {
                it.test()
            }
        }
        val test: Int get() {
            listOf<TestInterface>(Test.A).plus((0..15).map { Test.B(it) })
                .associateBy { Random.nextInt() }
            return 4
        }
    }
}

fun main() {
    listOf<Test<Int>>(Test.A, Test.B(3)).forEach {
        it.test()
    }
    listOf<TestInterface>(Test.A, Test.B(3)).forEach {
        it.test()
    }
}