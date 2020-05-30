package com.lightningkite.khrysalis.flow



class MergableCollection<T: Mergable<T>> (val items: ArrayList<T> = ArrayList()) :
    kotlin.collections.Collection<T> by items {
    fun add(item: T) {
        for (index in items.indices) {
            val e = items[index]
            val merged = item.merge(e)
            if (merged != null){
                items[index] = merged
                return
            }
        }
        items.add(item)
    }
    fun copy() = MergableCollection(ArrayList(items))
}