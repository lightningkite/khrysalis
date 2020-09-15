package com.lightningkite.khrysalis.views.draw

import android.graphics.Shader
import com.lightningkite.khrysalis.views.geometry.GFloat


fun newRadialGradient(
    centerX: GFloat,
    centerY: GFloat,
    radius: GFloat,
    colors: List<Int>,
    stops: List<GFloat>,
    tile: Shader.TileMode
) = android.graphics.RadialGradient(
    centerX,
    centerY,
    radius,
    colors.toIntArray(),
    stops.toFloatArray(),
    tile
)
