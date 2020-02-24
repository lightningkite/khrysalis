package com.lightningkite.khrysalis

fun <T> List<T>.withoutIndex(index: Int): List<T> {
    return this.toMutableList().apply { removeAt(index) }
}

fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
    var sum: Long = 0
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

inline fun <T, K : Comparable<K>> MutableList<T>.binaryInsertBy(
    item: T,
    crossinline selector: (T) -> K?
) {
    val index = binarySearchBy(selector(item), selector = selector)
    if (index < 0) {
        add(
            index = -index - 1,
            element = item
        )
    } else {
        add(
            index = index,
            element = item
        )
    }
}

inline fun <T, K : Comparable<K>> MutableList<T>.binaryInsertByDistinct(
    item: T,
    crossinline selector: (T) -> K?
): Boolean {
    val index = binarySearchBy(selector(item), selector = selector)
    if (index < 0) {
        add(
            index = -index - 1,
            element = item
        )
        return true
    } else {
        return false
    }
}


inline fun <T, K : Comparable<K>> List<T>.binaryFind(
    key: K,
    crossinline selector: (T) -> K?
): T? {
    val index = binarySearchBy(key, selector = selector)
    if(index >= 0){
        return this[index]
    } else {
        return null
    }
}


inline fun <T, K : Comparable<K>> List<T>.binaryForEach(
    lower: K,
    upper: K,
    crossinline selector: (T) -> K?,
    action: (T)->Unit
) {
    var index = binarySearchBy(lower, selector = selector)
    if(index < 0){
        index = -index - 1
    }
    while(index < size){
        val item = this[index]
        val itemK = selector(item)
        if(itemK == null || itemK > upper) break
        action(item)
        index++
    }
}
