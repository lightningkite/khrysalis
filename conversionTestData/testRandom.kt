@file:SharedCode
package com.test.random

import kotlin.random.Random
import com.lightningkite.butterfly.*
import com.lightningkite.butterfly.view.geometry.*

fun Random.nextFloat(until: Float): Float = this.nextDouble(until.toDouble()).toFloat()
fun Random.nextFloat(from: Float, until: Float): Float = this.nextDouble(from.toDouble(), until.toDouble()).toFloat()

fun Random.nextGFloat(): GFloat = this.nextDouble().toGFloat()
fun Random.nextGFloat(until: GFloat): GFloat = this.nextDouble(until.toDouble()).toGFloat()
fun Random.nextGFloat(from: GFloat, until: GFloat): GFloat = this.nextDouble(from.toDouble(), until.toDouble()).toGFloat()


fun main(){
    Random.nextFloat()
    Random.nextFloat(2f)
    Random.nextFloat(1f, 2f)
    Random.nextGFloat()
    Random.nextGFloat(2f)
    Random.nextGFloat(1f, 2f)
    Random.Default.nextFloat()
    Random.Default.nextFloat(2f)
    Random.Default.nextFloat(1f, 2f)
    Random.Default.nextGFloat()
    Random.Default.nextGFloat(2f)
    Random.Default.nextGFloat(1f, 2f)
}
