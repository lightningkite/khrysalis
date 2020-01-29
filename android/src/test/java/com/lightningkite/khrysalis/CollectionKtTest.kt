package com.lightningkite.khrysalis

import org.junit.Test

import org.junit.Assert.*

class CollectionKtTest {

    val orderedList = (0..100).asSequence().map { it * 2 }.toList()

    fun <T : Comparable<T>> Iterable<T>.isSorted(): Boolean {
        var lastItem: T? = null
        for (item in this) {
            if (lastItem != null) {
                if (item < lastItem) return false
            }
            lastItem = item
        }
        return true
    }

    @Test
    fun binaryInsertByTest() {
        for (i in -4..204) {
            assert(orderedList.toMutableList().apply {
                binaryInsertBy(i) { it }
            }.isSorted()) { "Inserting $i is not sorted" }
        }
    }

    @Test
    fun binaryFindTest() {
        for (i in -4..204) {
            assert(orderedList.find { it == i } == orderedList.binaryFind(i) { it })
        }
    }

    @Test
    fun binaryForEachTest() {
        for (i in -4..204) {
            for (j in i+1..204) {
                assert(orderedList.filter { it in i..j } == ArrayList<Int>().apply {
                    orderedList.binaryForEach(i, j, { it }){
                        add(it)
                    }
                })
            }
        }
    }
}
