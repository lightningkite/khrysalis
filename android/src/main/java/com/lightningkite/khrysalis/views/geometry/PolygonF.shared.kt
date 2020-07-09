package com.lightningkite.khrysalis.views.geometry

import android.graphics.PointF

data class PolygonF(val points: List<PointF>){
    fun contains(point: PointF): Boolean {
        var inside = false
        val big = 1000f
        for(index in 0 until points.size - 2){
            val a = points[index]
            val b = points[index + 1]
            val denom = - (big - point.x) * (b.y - a.y)
            if(denom == 0f) continue
            val ua = ((big - point.x) * (a.y - point.y)) / denom
            val ub = ((b.x - a.x) * (a.y - point.y) - (b.y - a.y) * (a.x - point.x)) / denom
            if(ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f) {
                inside = !inside
            }
        }
        return inside
    }
}