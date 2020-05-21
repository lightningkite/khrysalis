package com.test.collections

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
}