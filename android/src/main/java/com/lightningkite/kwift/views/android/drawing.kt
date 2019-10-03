package com.lightningkite.kwift.views.android

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint


fun Canvas.drawTextCentered(text: String, centerX: Float, centerY: Float, paint: Paint) {
    val textWidth = paint.measureText(text)
    val textHeightOffset = (paint.fontMetrics.let { it.ascent + it.descent } / 2)
    drawText(text, centerX - textWidth / 2, centerY - textHeightOffset, paint)
}

fun Int.colorAlpha(desiredAlpha: Int): Int = Color.argb(
    desiredAlpha,
    Color.red(this),
    Color.green(this),
    Color.blue(this)
)
