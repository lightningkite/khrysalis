package com.lightningkite.khrysalis.views.draw

import android.graphics.Shader
import com.lightningkite.khrysalis.views.geometry.GFloat

fun newLinearGradient(
    x0: GFloat,
    y0: GFloat,
    x1: GFloat,
    y1: GFloat,
    colors: List<Int>,
    positions: List<GFloat>,
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
