package com.lightningkite.khrysalis.views.geometry

typealias GFloat = Float
fun Float.toGFloat(): GFloat = this
fun Double.toGFloat(): GFloat = this.toFloat()
fun Byte.toGFloat(): GFloat = this.toFloat()
fun Short.toGFloat(): GFloat = this.toFloat()
fun Int.toGFloat(): GFloat = this.toFloat()
fun Long.toGFloat(): GFloat = this.toFloat()