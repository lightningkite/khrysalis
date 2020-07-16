package com.lightningkite.khrysalis.views.geometry

import android.graphics.Matrix
import android.graphics.PointF

fun Matrix.mapPoint(point: PointF): PointF {
    val dst = floatArrayOf(0f, 0f)
    val src = floatArrayOf(point.x, point.y)
    mapPoints(dst, src)
    return PointF(dst[0], dst[1])
}

fun Matrix.inverted(): Matrix = Matrix().also { this.invert(it) }
fun Matrix.setInvert(other: Matrix) {
    other.invert(this)
}