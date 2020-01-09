package com.lightningkite.kwift.views

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.DisplayMetrics
import android.view.View
import com.lightningkite.kwift.*
import com.lightningkite.kwift.observables.*
import com.lightningkite.kwift.floorDiv
import com.lightningkite.kwift.floorMod
import com.lightningkite.kwift.time.*
import com.lightningkite.kwift.views.draw.drawTextCentered
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.max

/**Renders a swipeable calendar.**/
open class SelectMultipleDatesMonthCVD : MonthCVD() {
    override fun generateAccessibilityView(): View? = null

    val dates: StandardObservableProperty<Set<DateAlone>> = StandardObservableProperty<Set<DateAlone>>(setOf())
    val selectedDayPaint: Paint = Paint()
    val selectedPaint: Paint = Paint()

    init {
        this.dates.onChange.addWeak(this) { self, value ->
            self.invalidate()
        }
    }

    override fun measure(width: Float, height: Float, displayMetrics: DisplayMetrics) {
        super.measure(width = width, height = height, displayMetrics = displayMetrics)
        selectedDayPaint.textSize = dayPaint.textSize
    }

    val drawDay_dateAlone: DateAlone = DateAlone(0,0,0)
    override fun drawDay(
        canvas: Canvas,
        showingMonth: DateAlone,
        day: DateAlone,
        displayMetrics: DisplayMetrics,
        outer: RectF,
        inner: RectF
    ) {
        if (dates.value.contains(day)) {
            val leftDate = drawDay_dateAlone.set(day).setAddDayOfMonth(-1)
            val left = dates.value.contains(leftDate)
            val rightDate = drawDay_dateAlone.set(day).setAddDayOfMonth(1)
            val right = dates.value.contains(rightDate)

            when {
                !left && !right -> {
                    CalendarDrawing.dayBackground(canvas, inner, selectedPaint)
                }
                !left && right -> {
                    CalendarDrawing.dayBackgroundStart(canvas, inner, outer, selectedPaint)
                }
                left && !right -> {
                    CalendarDrawing.dayBackgroundEnd(canvas, inner, outer, selectedPaint)
                }
                left && right -> {
                    CalendarDrawing.dayBackgroundMid(canvas, inner, outer, selectedPaint)
                }
                else -> {
                    CalendarDrawing.dayBackground(canvas, inner, selectedPaint)
                }
            }
            CalendarDrawing.day(canvas, showingMonth, day, inner, selectedDayPaint)
        } else {
            CalendarDrawing.day(canvas, showingMonth, day, inner, dayPaint)
        }
    }

    override fun onTap(day: DateAlone) {
        adding = dates.value.none { it -> day == it }
        onTouchMove(day)
    }

    var adding: Boolean = false
    override fun onTouchDown(day: DateAlone): Boolean {
        adding = dates.value.none { it -> day == it }
        onTouchMove(day)
        return true
    }

    override fun onTouchMove(day: DateAlone): Boolean {
        if (adding) {
            if (dates.value.none({ it -> day == it })) {
                dates.value = dates.value.plus(day)
            }
        } else {
            dates.value = dates.value.filter { it -> it != day }.toSet()
        }
        return true
    }

    override fun onTouchUp(day: DateAlone): Boolean {
        return true
    }
}
