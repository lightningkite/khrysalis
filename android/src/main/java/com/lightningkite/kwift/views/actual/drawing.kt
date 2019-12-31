package com.lightningkite.kwift.views.actual

import android.graphics.*


fun Canvas.drawTextCentered(text: String, centerX: Float, centerY: Float, paint: Paint) {
    val textWidth = paint.measureText(text)
    val textHeightOffset = paint.fontMetrics.let { it.ascent + it.descent } / 2
    drawText(text, centerX - textWidth / 2, centerY - textHeightOffset, paint)
}
fun Canvas.drawText(text: String, x: Float, y: Float, gravity: AlignPair, paint: Paint) {
    val textWidth = paint.measureText(text)
    val textHeight = paint.textHeight
    drawText(
        text,
        when(gravity.horizontal){
            Align.start -> x
            Align.fill, Align.center -> x - textWidth/2
            Align.end -> x - textWidth
        },
        when(gravity.vertical){
            Align.start -> y - paint.fontMetrics.ascent
            Align.fill, Align.center -> y - paint.fontMetrics.let { it.ascent + it.descent } / 2
            Align.end -> y - paint.fontMetrics.descent
        },
        paint
    )
}

fun LinearGradient(
    x0: Float,
    y0: Float,
    x1: Float,
    y1: Float,
    colors: List<Int>,
    positions: List<Float>,
    tile: Shader.TileMode
) = LinearGradient(
    x0,
    y0,
    x1,
    y1,
    colors.toIntArray(),
    positions.toFloatArray(),
    tile
)

val Paint.textHeight: Float get() = fontMetrics.let { it.descent - it.ascent }

fun Int.colorAlpha(desiredAlpha: Int): Int = Color.argb(
    desiredAlpha,
    Color.red(this),
    Color.green(this),
    Color.blue(this)
)

var tempRect: RectF = RectF()
fun Canvas.drawBitmap(bitmap: Bitmap, left: Float, top: Float, right: Float, bottom: Float){
    tempRect.left = left
    tempRect.top = top
    tempRect.right = right
    tempRect.bottom = bottom
    drawBitmap(bitmap, null, tempRect, null)
}

//fun Canvas.clear(left: Float, top: Float, right: Float, bottom: Float) {
//    this.
//}

/*

CUSTOM DRAWING

- Custom view w/ touch interaction
- Calendar view custom callback - perhaps override on both sides, and it'd be OK?

MonthView - Can take custom renderer and touch handler
CalendarView - Can take MonthView generator, show specific months

*/
