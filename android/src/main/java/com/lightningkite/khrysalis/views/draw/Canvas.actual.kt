package com.lightningkite.khrysalis.views.draw

import android.graphics.*
import com.lightningkite.khrysalis.views.geometry.Align
import com.lightningkite.khrysalis.views.geometry.AlignPair
import com.lightningkite.khrysalis.views.geometry.GFloat

/* SHARED DECLARATIONS
typealias Canvas = Canvas

fun Canvas.clipRect(left: GFloat, top: GFloat, right: GFloat, bottom: GFloat)
fun Canvas.clipRect(rect: RectF)
fun Canvas.drawCircle(cx: GFloat, cy: GFloat, radius: GFloat, paint: Paint)
fun Canvas.drawRect(left: GFloat, top: GFloat, right: GFloat, bottom: GFloat, paint: Paint)
fun Canvas.drawRect(rect: RectF, paint: Paint)
fun Canvas.drawOval(left: GFloat, top: GFloat, right: GFloat, bottom: GFloat, paint: Paint)
fun Canvas.drawOval(rect: RectF, paint: Paint)
fun Canvas.drawRoundRect(left: GFloat, top: GFloat, right: GFloat, bottom: GFloat, rx: GFloat, ry: GFloat, paint: Paint)
fun Canvas.drawRoundRect(rect: RectF, rx: GFloat, ry: GFloat, paint: Paint)
fun Canvas.drawLine(x1: GFloat, y1: GFloat, x2: GFloat, y2: GFloat, paint: Paint)
fun Canvas.drawPath(path: Path, paint: Paint)
fun Canvas.save()
fun Canvas.restore()
fun Canvas.translate(dx: GFloat, dy: GFloat)
fun Canvas.scale(scaleX: GFloat, scaleY: GFloat)
fun Canvas.rotate(degrees: GFloat)

 */

fun Canvas.drawTextCentered(text: String, centerX: GFloat, centerY: GFloat, paint: Paint) {
    val textWidth = paint.measureText(text)
    val textHeightOffset = paint.fontMetrics.let { it.ascent + it.descent } / 2
    drawText(text, centerX - textWidth / 2, centerY - textHeightOffset, paint)
}
fun Canvas.drawText(text: String, x: GFloat, y: GFloat, gravity: AlignPair, paint: Paint) {
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
fun Canvas.drawBitmap(bitmap: Bitmap, left: GFloat, top: GFloat, right: GFloat, bottom: GFloat){
    tempRect.left = left
    tempRect.top = top
    tempRect.right = right
    tempRect.bottom = bottom
    drawBitmap(bitmap, null, tempRect, null)
}

