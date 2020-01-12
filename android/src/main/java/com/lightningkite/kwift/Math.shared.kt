package com.lightningkite.kwift

fun Int.floorMod(other: Int): Int = (this % other + other) % other
fun Int.floorDiv(other: Int): Int {
    if(this < 0){
        return this / other - 1
    } else {
        return this / other
    }
}

fun Float.floorMod(other: Float): Float = (this % other + other) % other
fun Float.floorDiv(other: Float): Float {
    if(this < 0){
        return this / other - 1
    } else {
        return this / other
    }
}


fun Double.floorMod(other: Double): Double = (this % other + other) % other
fun Double.floorDiv(other: Double): Double {
    if(this < 0){
        return this / other - 1
    } else {
        return this / other
    }
}
