@file:SharedCode
package com.test.collections

import com.lightningkite.khrysalis.*
data class CustomEq(val value: Int)

fun main(vararg args: String){
    val list = listOf(1, 2, 3)
    println(list[0])

    val pair: Pair<Int, String> = 3 to "Three"
    println(pair.first)
    println(pair.second)

    println(list.map { it + 1 }.filter { it % 2 == 0 }.joinToString())
    println(list.firstOrNull() ?: "nah")
    println(list.singleOrNull() ?: "nah")

    listOf(1, 2, null, 4, 8).filterNotNull().forEach {
        println(it + 3)
    }

    val mutableList: MutableList<Int> = ArrayList()
    mutableList.add(1)
    mutableList.add(2)
    mutableList.add(0, 2)
    println("Mutable list add order check: " + mutableList.joinToString())
    val x = 0
    mutableList[x + 1] += 4
    mutableList[0] += 4
    println(mutableList[0])
    println(mutableList.size)

    val arrayList = ArrayList<Int>()
    arrayList.add(1)
    arrayList.add(2)
    arrayList.add(0, 2)
    println("Array list add order check: " + arrayList.joinToString())
    arrayList[x + 1] += 4
    arrayList[0] += 4
    println(arrayList[0])
    println(arrayList.size)

    println(listOf("a", "b", "c").joinToString())
    println(listOf("a", "b", "c").joinToString("|"))
    println(listOf("a", "b", "c").joinToString("|"){ it + "j" })

    val nested = ArrayList<ArrayList<Int>>()
    nested.add(ArrayList())
    nested[0].add(0)

    val seq = sequenceOf(1, 2, 3)

    sequenceOf(1, 2, 3)
        .map { it + 1 }
        .filter { it % 2 == 0 }
        .sorted()
        .forEach {
            println("Got $it")
        }

    println(setOf(1, 2, 3).sorted().joinToString())
    println(setOf("A", "B", "C").sorted().joinToString())
    println(setOf('a', 'b', 'c').sorted().joinToString())
    println(setOf(CustomEq(1), CustomEq(2), CustomEq(3)).sortedBy { it.value }.joinToString())

    println(mapOf(1 to 2, 3 to 4).entries.sortedBy { it.key }.joinToString { it.key.toString() + ":" + it.value.toString() })
    println(mapOf(CustomEq(1) to 2, CustomEq(3) to 4).entries.sortedBy { it.key.value }.joinToString { it.key.toString() + ":" + it.value.toString() })

    println(mutableSetOf(1, 2, 3).sorted().joinToString())
    println(mutableSetOf("A", "B", "C").sorted().joinToString())
    println(mutableSetOf('a', 'b', 'c').sorted().joinToString())
    println(mutableSetOf(CustomEq(1), CustomEq(2), CustomEq(3)).sortedBy { it.value }.joinToString())

    println(mutableMapOf(1 to 2, 3 to 4).entries.sortedBy { it.key }.joinToString { it.key.toString() + ":" + it.value.toString() })
    println(mutableMapOf(CustomEq(1) to 2, CustomEq(3) to 4).entries.sortedBy { it.key.value }.joinToString { it.key.toString() + ":" + it.value.toString() })

    println(hashSetOf(1, 2, 3).sorted().joinToString())
    println(hashSetOf("A", "B", "C").sorted().joinToString())
    println(hashSetOf('a', 'b', 'c').sorted().joinToString())
    println(hashSetOf(CustomEq(1), CustomEq(2), CustomEq(3)).sortedBy { it.value }.joinToString())

    println(hashMapOf(1 to 2, 3 to 4).entries.sortedBy { it.key }.joinToString { it.key.toString() + ":" + it.value.toString() })
    println(hashMapOf(CustomEq(1) to 2, CustomEq(3) to 4).entries.sortedBy { it.key.value }.joinToString { it.key.toString() + ":" + it.value.toString() })

    HashSet<Int>()
    HashSet<CustomEq>()

    HashMap<Int, String>()
    HashMap<CustomEq, String>()

    ArrayList<Int>()

    val myMap = HashMap<Int, Int>()
    myMap[1] = 2
    myMap.put(2, 3)
    println(myMap[0] ?: "nah")
    myMap.getOrPut(3) { 4 }
    println(myMap.entries.sortedBy { it.key }.joinToString { it.key.toString() + ":" + it.value.toString() })
}