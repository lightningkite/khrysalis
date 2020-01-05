package com.lightningkite.kwift.views.draw

import android.graphics.*
import com.lightningkite.kwift.views.geometry.Align
import com.lightningkite.kwift.views.geometry.AlignPair

/* SHARED DECLARATIONS
typealias Canvas = Canvas

fun Canvas.clipRect(left: Float, top: Float, right: Float, bottom: Float)
fun Canvas.clipRect(rect: RectF)
fun Canvas.drawCircle(cx: Float, cy: Float, radius: Float, paint: Paint)
fun Canvas.drawRect(left: Float, top: Float, right: Float, bottom: Float, paint: Paint)
fun Canvas.drawRect(rect: RectF, paint: Paint)
fun Canvas.drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: Paint)
fun Canvas.drawOval(rect: RectF, paint: Paint)
fun Canvas.drawRoundRect(left: Float, top: Float, right: Float, bottom: Float, rx: Float, ry: Float, paint: Paint)
fun Canvas.drawRoundRect(rect: RectF, rx: Float, ry: Float, paint: Paint)
fun Canvas.drawLine(x1: Float, y1: Float, x2: Float, y2: Float, paint: Paint)
fun Canvas.drawPath(path: Path, paint: Paint)
fun Canvas.save()
fun Canvas.restore()
fun Canvas.translate(dx: Float, dy: Float)
fun Canvas.scale(scaleX: Float, scaleY: Float)
fun Canvas.rotate(degrees: Float)

 */

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


var tempRect: RectF = RectF()
fun Canvas.drawBitmap(bitmap: Bitmap, left: Float, top: Float, right: Float, bottom: Float){
    tempRect.left = left
    tempRect.top = top
    tempRect.right = right
    tempRect.bottom = bottom
    drawBitmap(bitmap, null, tempRect, null)
}


/*

CUSTOM DRAWING

- Custom view w/ touch interaction
- Calendar view custom callback - perhaps override on both sides, and it'd be OK?

MonthView - Can take custom renderer and touch handler
CalendarView - Can take MonthView generator, show specific months

*/
