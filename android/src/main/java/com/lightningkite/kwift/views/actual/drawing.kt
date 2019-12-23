package com.lightningkite.kwift.views.actual

import android.graphics.*


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
