package com.lightningkite.khrysalis.flow

fun <E> MutableList<E>.addOrMerge(
    item: E,
    satisfies: (E, E)->E?
) {
    for(otherIndex in indices){
        val r = satisfies(item, this[otherIndex])
        if(r != null) {
            this[otherIndex] = r
            return
        }
    }
    this.add(item)
}