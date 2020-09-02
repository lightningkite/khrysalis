package com.test

private data class CollectionsCustomEq(val value: Int)
private data class CollectionBox<T>(val value: T)
private data class CollectionMutBox<T>(var value: T)

private fun collectionsTest(){
    var list = listOf(1, 2, 3)
    list += 4
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
    mutableList += 4
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
    arrayList.addAll(2, listOf(1, 2))
    arrayList.addAll(listOf(1, 2))
    arrayList += 99
    arrayList += listOf(99)
    arrayList[x + 1] += 4
    arrayList[0] += 4
    println(arrayList[0])
    println(arrayList.size)

    val arrayListBox = CollectionBox(arrayList)
    arrayListBox.value += 2

    val listBox = CollectionMutBox(listOf(1, 2, 3))
    arrayListBox.value += 4

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

    val maybeSeq = if(x == 0) sequenceOf(1, 2, 3) else null

    maybeSeq
        ?.map { it + 1 }
        ?.filter { it % 2 == 0 }
        ?.sorted()
        ?.toList()
        ?.forEach {
            println("Got $it")
        }

    maybeSeq
        ?.map { it + 1 }
        ?.filter { it % 2 == 0 }
        ?.sorted()
        ?.forEach {
            println("Got $it")
        }

    println(setOf(1, 2, 3))
    println(setOf("A", "B", "C"))
    println(setOf('a', 'b', 'c'))
    println(setOf(CollectionsCustomEq(1), CollectionsCustomEq(2), CollectionsCustomEq(3)))

    println(mapOf(1 to 2, 3 to 4))
    println(mapOf(CollectionsCustomEq(1) to 2, CollectionsCustomEq(3) to 4))

    println(mutableSetOf(1, 2, 3))
    println(mutableSetOf("A", "B", "C"))
    println(mutableSetOf('a', 'b', 'c'))
    println(mutableSetOf(CollectionsCustomEq(1), CollectionsCustomEq(2), CollectionsCustomEq(3)))

    println(mutableMapOf(1 to 2, 3 to 4))
    println(mutableMapOf(CollectionsCustomEq(1) to 2, CollectionsCustomEq(3) to 4))

    println(hashSetOf(1, 2, 3))
    println(hashSetOf("A", "B", "C"))
    println(hashSetOf('a', 'b', 'c'))
    println(hashSetOf(CollectionsCustomEq(1), CollectionsCustomEq(2), CollectionsCustomEq(3)))

    println(hashMapOf(1 to 2, 3 to 4))
    println(hashMapOf(CollectionsCustomEq(1) to 2, CollectionsCustomEq(3) to 4))

    println(HashSet<Int>())
    println(HashSet<CollectionsCustomEq>())

    println(HashMap<Int, String>())
    println(HashMap<CollectionsCustomEq, String>())

    println(ArrayList<Int>())

    listOf(1, 2, 3, 4, 5).groupBy { it % 2 }

    for((key, value) in mapOf(1 to 2, 3 to 4)){
        println("$key: $value")
    }
}