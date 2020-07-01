package com.test.collections

data class CustomEq(val value: Int)

fun test(){
    val list = listOf(1, 2, 3)
    println(list[0])

    val pair: Pair<Int, String> = 3 to "Three"
    println(pair.first)
    println(pair.second)

    println(list.map { it + 1 }.filter { it % 2 == 0 })
    println(list.firstOrNull())
    println(list.singleOrNull())

    listOf(1, 2, null, 4, 8).filterNotNull().forEach {
        println(it + 3)
    }

    val mutableList: MutableList<Int> = ArrayList()
    mutableList.add(1)
    mutableList.add(2)
    mutableList.add(0, 2)
    val x = 0
    mutableList[x + 1] += 4
    mutableList[0] += 4
    println(mutableList[0])
    println(mutableList.size)

    val arrayList = ArrayList<Int>()
    arrayList.add(1)
    arrayList.add(2)
    arrayList.add(0, 2)
    arrayList[x + 1] += 4
    arrayList[0] += 4
    println(arrayList[0])
    println(arrayList.size)

    println(listOf("a", "b", "c").joinToString())
    println(listOf("a", "b", "c").joinToString("|"))
    println(listOf("a", "b", "c").joinToString("|"){ it + "j" })

    sequenceOf(1, 2, 3)
        .map { it + 1 }
        .filter { it % 2 == 0 }
        .sorted()
        .forEach {
            println("Got $it")
        }

    println(setOf(1, 2, 3))
    println(setOf("A", "B", "C"))
    println(setOf('a', 'b', 'c'))
    println(setOf(CustomEq(1), CustomEq(2), CustomEq(3)))

    println(mapOf(1 to 2, 3 to 4))
    println(mapOf(CustomEq(1) to 2, CustomEq(3) to 4))

    println(mutableSetOf(1, 2, 3))
    println(mutableSetOf("A", "B", "C"))
    println(mutableSetOf('a', 'b', 'c'))
    println(mutableSetOf(CustomEq(1), CustomEq(2), CustomEq(3)))

    println(mutableMapOf(1 to 2, 3 to 4))
    println(mutableMapOf(CustomEq(1) to 2, CustomEq(3) to 4))

    println(hashSetOf(1, 2, 3))
    println(hashSetOf("A", "B", "C"))
    println(hashSetOf('a', 'b', 'c'))
    println(hashSetOf(CustomEq(1), CustomEq(2), CustomEq(3)))

    println(hashMapOf(1 to 2, 3 to 4))
    println(hashMapOf(CustomEq(1) to 2, CustomEq(3) to 4))

    println(HashSet<Int>())
    println(HashSet<CustomEq>())

    println(HashMap<Int, String>())
    println(HashMap<CustomEq, String>())

    println(ArrayList<Int>())
}