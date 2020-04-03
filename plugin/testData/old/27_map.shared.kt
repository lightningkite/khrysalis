package com.test

import java.util.*

fun main() {
    // Constructors
    val hashMap0 = HashMap<String, String>()
    val linkedHashMap0 = LinkedHashMap<String, String>()
    val map0 = emptyMap<String, String>()
    val map1 = mapOf(10 to "hi")
    val map2 = mapOf("as" to "hi", "df" to "hello", "gh" to "salut")
    var hashMap = hashMapOf(1 to "hi")
    var linkedMap = linkedMapOf(1 to "hi")
    var mutableMap = mutableMapOf(1 to "hi")

    // Basic calls
    println("${map1[10]} (hi)")
    println("${map2["as"]} (hi)")
    println("${map2.size} (3)")
    hashMap.put(2, "hello")
    linkedMap.putAll(linkedMap)
    mutableMap.remove(1)

    println(hashMap)
    if (hashMap.size != 2) {
        println("ERROR: hashMap.size")
    }
    println(linkedMap)
    if (linkedMap.size != 1) {
        println("ERROR: linkedMap.size")
    }
    println(mutableMap)
    if (mutableMap.size != 0) {
        println("ERROR: mutableMap.size")
    }

    if (!map0.isEmpty()) {
        println("ERROR: map0.isEmpty()")
    }

    println("${hashMap.keys} ([2, 1])")
    println("${hashMap.values} ([hello, hi])")

    // Iteration
    for ((k, v) in map2) {
        println("$k : $v")
    }
}
