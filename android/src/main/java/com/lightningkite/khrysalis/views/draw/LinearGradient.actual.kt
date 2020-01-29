package com.lightningkite.khrysalis.views.draw

import android.graphics.Shader

fun LinearGradient(
    x0: Float,
    y0: Float,
    x1: Float,
    y1: Float,
    colors: List<Int>,
    positions: List<Float>,
    tile: Shader.TileMode
) = android.graphics.LinearGradient(
    x0,
    y0,
    x1,
    y1,
    colors.toIntArray(),
    positions.toFloatArray(),
    tile
)
