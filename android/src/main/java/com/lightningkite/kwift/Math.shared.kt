package com.lightningkite.kwift

fun Int.floorMod(other: Int): Int = (this % other + other) % other

fun Int.floorDiv(other: Int): Int {
    if(this < 0){
        return this / other - 1
    } else {
        return this / other
    }
}
