package com.lightningkite.khrysalis.views.geometry

object Geometry {

    fun rayIntersectsLine(
        rayX: GFloat,
        rayY: GFloat,
        rayToX: GFloat,
        rayToY: GFloat,
        lineX1: GFloat,
        lineY1: GFloat,
        lineX2: GFloat,
        lineY2: GFloat
    ): Boolean {
        val denom: GFloat = (rayToY - rayY) * (lineX2 - lineX1) - (rayToX - rayX) * (lineY2 - lineY1)
        if (denom == 0f) return false
        val lineRatio: GFloat = ((rayToX - rayX) * (lineY1 - rayY) - (rayToY - rayY) * (lineX1 - rayX)) / denom
        val rayRatio: GFloat = ((lineX2 - lineX1) * (lineY1 - rayY) - (lineY2 - lineY1) * (lineX1 - rayX)) / denom
        return lineRatio >= 0.0f && lineRatio <= 1.0f && rayRatio > 0f

    }
}