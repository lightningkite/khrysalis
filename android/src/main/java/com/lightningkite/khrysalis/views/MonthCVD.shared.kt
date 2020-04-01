package com.lightningkite.khrysalis.views

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.DisplayMetrics
import android.view.View
import com.lightningkite.khrysalis.*
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.floorDiv
import com.lightningkite.khrysalis.floorMod
import com.lightningkite.khrysalis.rx.addWeak
import com.lightningkite.khrysalis.rx.forever
import com.lightningkite.khrysalis.time.*
import com.lightningkite.khrysalis.views.draw.drawTextCentered
import io.reactivex.rxkotlin.subscribeBy
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

/**Renders a swipeable calendar.**/
open class MonthCVD : CustomViewDelegate() {
    override fun generateAccessibilityView(): View? = null

    val currentMonthObs: MutableObservableProperty<DateAlone> =
        StandardObservableProperty(Date().dateAlone.setDayOfMonth(1))
    var currentMonth: DateAlone
        get() = currentMonthObs.value
        set(value) {
            currentMonthObs.value = value
        }

    var dragEnabled: Boolean = true

    init {
        this.currentMonthObs.subscribeBy @weakSelf { value ->
            this?.postInvalidate()
        }.forever()

    }

    var labelFontSp: Float = 12f
    var dayFontSp: Float = 16f
    var internalPaddingDp: Float = 8f
    var dayCellMarginDp: Float = 8f
    private var internalPadding: Float = 0f
    private var dayLabelHeight: Float = 0f
    private var dayCellHeight: Float = 0f
    private var dayCellWidth: Float = 0f
    private var dayCellMargin: Float = 0f

    private var _currentOffset: Float = 0f //ratio of widths
    var currentOffset: Float //ratio of widths
        get() {
            return _currentOffset
        }
        set(value) {
            _currentOffset = value
            customView?.postInvalidate()
        }
    private var dragStartX: Float = 0f
    private var lastOffset: Float = 0f
    private var lastOffsetTime: Long = 0L
    private val DRAGGING_NONE: Int = -1
    private var draggingId: Int = DRAGGING_NONE

    fun animateNextMonth() {
        currentMonthObs.value.setAddMonthOfYear(1)
        currentMonthObs.update()
        currentOffset = 1f
    }

    fun animatePreviousMonth() {
        currentMonthObs.value.setAddMonthOfYear(-1)
        currentMonthObs.update()
        currentOffset = -1f
    }

    val labelPaint: Paint = Paint()
    val dayPaint: Paint = Paint()

    init {
        this.labelPaint.isAntiAlias = true
        this.labelPaint.style = Paint.Style.FILL
        this.labelPaint.color = 0xFF808080.asColor()
        this.dayPaint.isAntiAlias = true
        this.dayPaint.style = Paint.Style.FILL
        this.dayPaint.color = 0xFF202020.asColor()
        animationFrame.addWeak(this) { self, timePassed ->
            if (this.draggingId == DRAGGING_NONE && this.currentOffset != 0f) {
                var newOffset = this.currentOffset * max(0f, (1f - 8f * timePassed))
                val min = 0.001f
                when {
                    newOffset > min -> newOffset -= min
                    newOffset < -min -> newOffset += min
                    else -> newOffset = 0f
                }
                this.currentOffset = newOffset
            }
        }
    }

    private val calcMonth: DateAlone =
        DateAlone(1, 1, 1)

    fun dayAtPixel(x: Float, y: Float, existing: DateAlone? = null): DateAlone? {
        if (y < dayLabelHeight) return null
//        val columnRaw = (x / dayCellWidth - (dayCellWidth + currentOffset) * 7).toInt()
        val columnRawBeforeDrag = x / dayCellWidth
        val columnDrag = currentOffset * 7
        val columnRaw = (columnDrag + columnRawBeforeDrag).toInt()
        val column = columnRaw.floorMod(7)
        val monthOffset = columnRaw.floorDiv(7)
        val row = ((y - dayLabelHeight) / dayCellHeight).toInt()
        if (row < 0 || row > 5) return null
        if (column < 0 || column > 6) return null
        return dayAt(
            calcMonth.set(currentMonth).setAddMonthOfYear(monthOffset),
            row,
            column,
            existing ?: DateAlone(0, 0, 0)
        )
    }

    fun dayAt(
        month: DateAlone, row: Int, column: Int, existing: DateAlone = DateAlone(
            0,
            0,
            0
        )
    ): DateAlone {
        return existing
            .set(month)
            .setDayOfMonth(1)
            .setDayOfWeek(1)
            .setAddDayOfMonth(row * 7 + column)
    }

    open fun measure(width: Float, height: Float, displayMetrics: DisplayMetrics) {
        internalPadding = displayMetrics.density * internalPaddingDp
        dayCellMargin = displayMetrics.density * dayCellMarginDp
        labelPaint.textSize = labelFontSp * displayMetrics.scaledDensity
        dayPaint.textSize = dayFontSp * displayMetrics.scaledDensity
        dayLabelHeight = labelPaint.textSize * 1.5f + internalPadding * 2
        dayCellWidth = width / 7f
        dayCellHeight = (height - dayLabelHeight) / 6f
    }

    private val calcMonthB: DateAlone =
        DateAlone(0, 0, 0)

    override fun draw(canvas: Canvas, width: Float, height: Float, displayMetrics: DisplayMetrics) {
        measure(width, height, displayMetrics)
        if (currentOffset > 0f) {
            //draw past month and current month
            drawMonth(
                canvas,
                (currentOffset - 1f) * width,
                width,
                calcMonthB.set(currentMonth).setAddMonthOfYear(-1),
                displayMetrics
            )
            drawMonth(canvas, currentOffset * width, width, currentMonth, displayMetrics)
        } else if (currentOffset < 0f) {
            //draw future month and current month
            drawMonth(
                canvas,
                (currentOffset + 1f) * width,
                width,
                calcMonthB.set(currentMonth).setAddMonthOfYear(1),
                displayMetrics
            )
            drawMonth(canvas, currentOffset * width, width, currentMonth, displayMetrics)
        } else {
            //Nice, it's exactly zero.  We can just draw one.
            drawMonth(canvas, currentOffset * width, width, currentMonth, displayMetrics)
        }
    }

    private var drawDate: DateAlone =
        DateAlone(1, 1, 1)
    private val rectForReuse: RectF = RectF()
    private val rectForReuseB: RectF = RectF()
    open fun drawMonth(canvas: Canvas, xOffset: Float, width: Float, month: DateAlone, displayMetrics: DisplayMetrics) {
        for (day in 1.toInt()..7.toInt()) {
            val col = day - 1
            rectForReuse.set(
                xOffset + col.toFloat() * dayCellWidth - 0.01f,
                -0.01f,
                xOffset + (col.toFloat() + 1) * dayCellWidth + 0.01f,
                dayLabelHeight + 0.01f
            )
            rectForReuseB.set(rectForReuse)
            rectForReuse.inset(internalPadding, internalPadding)
            drawLabel(canvas, day, displayMetrics, rectForReuse, rectForReuseB)
        }
        for (row in 0.toInt()..5.toInt()) {
            for (col in 0.toInt()..6.toInt()) {
                val day = dayAt(month, row, col, drawDate)
                rectForReuse.set(
                    xOffset + col.toFloat() * dayCellWidth - 0.01f,
                    dayLabelHeight + row.toFloat() * dayCellHeight - 0.01f,
                    xOffset + (col.toFloat() + 1) * dayCellWidth + 0.01f,
                    dayLabelHeight + (row.toFloat() + 1) * dayCellHeight + 0.01f
                )
                if (rectForReuse.left > width) {
                    continue
                }
                if (rectForReuse.right < 0) {
                    continue
                }
                rectForReuseB.set(rectForReuse)
                rectForReuse.inset(dayCellMargin, dayCellMargin)
                drawDay(
                    canvas = canvas,
                    showingMonth = month,
                    day = day,
                    displayMetrics = displayMetrics,
                    outer = rectForReuseB,
                    inner = rectForReuse
                )
            }
        }
    }

    open fun drawLabel(canvas: Canvas, dayOfWeek: Int, displayMetrics: DisplayMetrics, outer: RectF, inner: RectF) {
        CalendarDrawing.label(canvas, dayOfWeek, inner, labelPaint)
    }

    open fun drawDay(
        canvas: Canvas,
        showingMonth: DateAlone,
        day: DateAlone,
        displayMetrics: DisplayMetrics,
        outer: RectF,
        inner: RectF
    ) {
        CalendarDrawing.day(canvas, showingMonth, day, outer, dayPaint)
    }

    var isTap: Boolean = false
    var dragStartY: Float = 0f
    open fun onTap(day: DateAlone) {}

    override fun onTouchDown(id: Int, x: Float, y: Float, width: Float, height: Float): Boolean {
        val day = dayAtPixel(x, y)
        day?.let {
            if (onTouchDown(it)) {
                return true
            }
        }
        dragStartX = x / width
        dragStartY = y / height
        draggingId = id
        lastOffsetTime = System.currentTimeMillis()
        isTap = true

        return true
    }

    open fun onTouchDown(day: DateAlone): Boolean = false
    override fun onTouchMove(id: Int, x: Float, y: Float, width: Float, height: Float): Boolean {
        if (draggingId == id) {
            lastOffset = currentOffset
            lastOffsetTime = System.currentTimeMillis()
            if (dragEnabled) {
                currentOffset = (x / width) - dragStartX
                if ((x / width - dragStartX).absoluteValue > 0.05f || (y / height - dragStartY).absoluteValue > 0.05f) {
                    isTap = false
                }
            }
        } else {
            dayAtPixel(x, y)?.let {
                return onTouchMove(it)
            }
        }
        return true
    }

    open fun onTouchMove(day: DateAlone): Boolean = false
    override fun onTouchUp(id: Int, x: Float, y: Float, width: Float, height: Float): Boolean {
        if (draggingId == id) {
            if (isTap) {
                dayAtPixel(x, y)?.let {
                    onTap(it)
                }
            } else if (dragEnabled) {
                val weighted =
                    currentOffset + (currentOffset - lastOffset) * 200f / (System.currentTimeMillis() - lastOffsetTime).toFloat()
                if (weighted > 0.5f) {
                    //shift right one
                    currentMonthObs.value.setAddMonthOfYear(-1)
                    currentMonthObs.update()
                    currentOffset -= 1
                } else if (weighted < -0.5f) {
                    //shift left one
                    currentMonthObs.value.setAddMonthOfYear(1)
                    currentMonthObs.update()
                    currentOffset += 1
                }
            }
            draggingId = DRAGGING_NONE
        } else {
            dayAtPixel(x, y)?.let {
                return onTouchUp(it)
            }
        }
        return true
    }

    open fun onTouchUp(day: DateAlone): Boolean = false

    override fun sizeThatFitsWidth(width: Float, height: Float): Float {
        return dayLabelHeight * 28f
    }

    override fun sizeThatFitsHeight(width: Float, height: Float): Float {
        return width * 6f / 7f + dayLabelHeight
    }
}

object CalendarDrawing {
    fun day(canvas: Canvas, month: DateAlone, date: DateAlone, inner: RectF, paint: Paint) {
        if (date.month == month.month && date.year == month.year) {
            canvas.drawTextCentered(
                date.day.toString(),
                inner.centerX(),
                inner.centerY(),
                paint
            )
        } else {
            val originalColor = paint.color
            @Suppress("CanBeVal") var myPaint = paint
            myPaint.color = paint.color.colorAlpha(64)
            canvas.drawTextCentered(
                date.day.toString(),
                inner.centerX(),
                inner.centerY(),
                myPaint
            )
            myPaint.color = originalColor
        }
    }

    fun label(canvas: Canvas, dayOfWeek: Int, inner: RectF, paint: Paint) {
        val text = TimeNames.shortWeekdayName(dayOfWeek)
        canvas.drawTextCentered(text, inner.centerX(), inner.centerY(), paint)
    }

    fun dayBackground(canvas: Canvas, inner: RectF, paint: Paint) {
        canvas.drawCircle(inner.centerX(), inner.centerY(), min(inner.width() / 2f, inner.height() / 2f), paint)
    }

    fun dayBackgroundStart(canvas: Canvas, inner: RectF, outer: RectF, paint: Paint) {
        canvas.drawCircle(inner.centerX(), inner.centerY(), min(inner.width() / 2f, inner.height() / 2f), paint)
        canvas.drawRect(outer.centerX(), inner.top, outer.right, inner.bottom, paint)
    }

    fun dayBackgroundMid(canvas: Canvas, inner: RectF, outer: RectF, paint: Paint) {
        canvas.drawRect(outer.left, inner.top, outer.right, inner.bottom, paint)
    }

    fun dayBackgroundEnd(canvas: Canvas, inner: RectF, outer: RectF, paint: Paint) {
        canvas.drawCircle(inner.centerX(), inner.centerY(), min(inner.width() / 2f, inner.height() / 2f), paint)
        canvas.drawRect(outer.left, inner.top, outer.centerX(), inner.bottom, paint)
    }
}

