package com.lightningkite.khrysalis.utils

import org.jetbrains.kotlin.KotlinParser

fun <T> virtualEnds(
    preItem: (T)->Unit,
    coreAction: ()->Unit,
    postItem: (T)->Unit,
    items: List<T>
){
    for(item in items.asReversed()){
        preItem(item)
    }
    coreAction()
    for(item in items){
        postItem(item)
    }
}
