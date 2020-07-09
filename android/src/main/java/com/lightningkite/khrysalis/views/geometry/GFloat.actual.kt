package com.lightningkite.khrysalis.views.geometry

typealias GFloat = Float
fun Float.toGFloat(): GFloat = this
fun Double.toGFloat(): GFloat = this.toFloat()