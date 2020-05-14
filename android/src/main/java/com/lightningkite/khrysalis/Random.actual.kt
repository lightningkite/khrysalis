package com.lightningkite.khrysalis

import kotlin.random.Random



fun Random.nextFloat(until: Float): Float = this.nextDouble(until.toDouble()).toFloat()
fun Random.nextFloat(from: Float, until: Float): Float = this.nextDouble(from.toDouble(), until.toDouble()).toFloat()

/* SHARED DECLARATIONS
class Random(seed: Long) {
    companion object : Random { }
    fun nextInt(): Int
    fun nextInt(until: Int): Int
    fun nextInt(from: Int, until: Int): Int
    fun nextLong(): Long
    fun nextLong(until: Long): Long
    fun nextLong(from: Long, until: Long): Long
    fun nextBoolean(): Boolean
    fun nextDouble(): Double
    fun nextDouble(until: Double): Double
    fun nextDouble(from: Double, until: Double): Double
    fun nextFloat(): Float
    fun nextFloat(until: Float): Float
    fun nextFloat(from: Float, until: Float): Float
}

 */
