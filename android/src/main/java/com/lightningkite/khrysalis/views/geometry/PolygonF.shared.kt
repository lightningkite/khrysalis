package com.lightningkite.khrysalis.views.geometry

import android.graphics.PointF
import com.lightningkite.khrysalis.views.geometry.Geometry

data class PolygonF(val points: List<PointF>){
    fun contains(point: PointF): Boolean {
        val intersections = this.points.indices.count {
            val a = points[it]
            val b = points[(it + 1) % points.size]
            Geometry.rayIntersectsLine(
                rayX = point.x,
                rayY = point.y,
                rayToX = point.x + 100f,
                rayToY = point.y,
                lineX1 = a.x,
                lineY1 = a.y,
                lineX2 = b.x,
                lineY2 = b.y
            )
        }
        return intersections % 2 == 1
    }
}
