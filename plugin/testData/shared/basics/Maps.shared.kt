package com.test

fun main(){
    val map: Map<Int, String> = mapOf(
        1 to "A",
        2 to "B",
        3 to "C"
    )
    assert(map[1] == "A")
    assert(map[2] == "B")
    assert(map[3] == "C")
    assert(map[0] == null)

    val mutableMap: MutableMap<Int, String> = mutableMapOf(
        1 to "A",
        2 to "B",
        3 to "C"
    )
    assert(mutableMap[1] == "A")
    assert(mutableMap[2] == "B")
    assert(mutableMap[3] == "C")
    assert(mutableMap[0] == null)
    mutableMap[0] = "-"
    assert(mutableMap[0] == "-")
    mutableMap.remove(0)
    assert(mutableMap[0] == null)
    mutableMap.put(0, "x")
    assert(mutableMap[0] == "x")
}
