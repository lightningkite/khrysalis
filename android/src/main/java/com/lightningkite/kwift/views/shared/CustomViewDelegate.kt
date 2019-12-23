package com.lightningkite.kwift.views.shared

import android.graphics.Canvas
import android.view.View

interface CustomViewDelegate {
    fun generateAccessibilityView(): View?
    fun draw(canvas: Canvas, width: Float, height: Float)
    fun onTouchDown(id: Int, x: Float, y: Float, width: Float, height: Float): Boolean = false
    fun onTouchMove(id: Int, x: Float, y: Float, width: Float, height: Float): Boolean = false
    fun onTouchUp(id: Int, x: Float, y: Float, width: Float, height: Float): Boolean = false
}
