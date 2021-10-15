package com.lightningkite.khrysalis

import com.lightningkite.khrysalis.views.geometry.GFloat
import com.lightningkite.khrysalis.views.geometry.toGFloat
import kotlin.random.Random

fun Random.nextFloat(until: Float): Float = this.nextDouble(until.toDouble()).toFloat()
fun Random.nextFloat(from: Float, until: Float): Float = this.nextDouble(from.toDouble(), until.toDouble()).toFloat()

fun Random.nextGFloat(): GFloat = this.nextDouble().toGFloat()
fun Random.nextGFloat(until: GFloat): GFloat = this.nextDouble(until.toDouble()).toGFloat()
fun Random.nextGFloat(from: GFloat, until: GFloat): GFloat = this.nextDouble(from.toDouble(), until.toDouble()).toGFloat()
