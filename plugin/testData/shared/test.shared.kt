package com.lightningkite.spintennis.engine

import android.graphics.Canvas
import kotlin.math.*


data class Point(val x: Float, val y: Float) {
    operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
}

operator fun Point.minus(other: Point): Point = Point(x - other.x, y - other.y)
operator fun Point.unaryMinus(): Point = Point(-x, -y)
