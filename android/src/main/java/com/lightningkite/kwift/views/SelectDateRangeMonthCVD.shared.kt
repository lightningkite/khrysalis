package com.lightningkite.kwift.views

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.DisplayMetrics
import android.view.View
import com.lightningkite.kwift.observables.*
import com.lightningkite.kwift.rx.addWeak
import com.lightningkite.kwift.time.*
import java.util.*

/**Renders a swipeable calendar.**/
open class SelectDateRangeMonthCVD : MonthCVD() {
    override fun generateAccessibilityView(): View? = null

    var draggingStart: Boolean = true
    var start: MutableObservableProperty<DateAlone?> = StandardObservableProperty(null)
    var endInclusive: MutableObservableProperty<DateAlone?> = StandardObservableProperty(null)
    init {
        this.start.onChange.addWeak(this) { self, value ->
            self.invalidate()
        }
        this.endInclusive.onChange.addWeak(this) { self, value ->
            self.invalidate()
        }
    }

    val selectedDayPaint: Paint = Paint()
    val selectedPaint: Paint = Paint()

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
        when {
            day == start.value && (day == endInclusive.value || endInclusive.value == null) -> {
                CalendarDrawing.dayBackground(canvas, inner, selectedPaint)
                CalendarDrawing.day(canvas, showingMonth, day, inner, selectedDayPaint)
            }
            day == start.value -> {
                CalendarDrawing.dayBackgroundStart(canvas, inner, outer, selectedPaint)
                CalendarDrawing.day(canvas, showingMonth, day, inner, selectedDayPaint)
            }
            day == endInclusive.value -> {
                CalendarDrawing.dayBackgroundEnd(canvas, inner, outer, selectedPaint)
                CalendarDrawing.day(canvas, showingMonth, day, inner, selectedDayPaint)
            }
            day.comparable > (start.value?.comparable ?: Int.MAX_VALUE) && day.comparable < (endInclusive.value?.comparable ?: Int.MIN_VALUE) -> {
                CalendarDrawing.dayBackgroundMid(canvas, inner, outer, selectedPaint)
                CalendarDrawing.day(canvas, showingMonth, day, inner, selectedDayPaint)
            }
            else -> {
                CalendarDrawing.day(canvas, showingMonth, day, inner, dayPaint)
            }
        }
    }


    private var startedDraggingOn: DateAlone? = null

    override fun onTap(day: DateAlone) {
        if(start.value != null && start.value == endInclusive.value && day.comparable > start.value!!.comparable){
            endInclusive.value = day
        } else {
            start.value = day
            endInclusive.value = day
        }
    }

    override fun onTouchDown(day: DateAlone): Boolean {
        if(day != start.value && day != endInclusive.value){
            return false
        }
        startedDraggingOn = day
        //If on start/end - drag
        //If after, extend
        //If before, extend
        //If middle, collapse all
        val startValue = start.value
        val endInclusiveValue = endInclusive.value

        when {
            startValue == null || endInclusiveValue == null -> {
                start.value = day
                endInclusive.value = day
                draggingStart = false
            }
            day == endInclusiveValue -> {
                draggingStart = false
            }
            day == startValue -> {
                draggingStart = true
            }
            day.comparable > endInclusiveValue!!.comparable && startValue == endInclusiveValue -> {
                endInclusive.value = day
                draggingStart = false
            }
            else -> {
                start.value = day
                endInclusive.value = day
                draggingStart = false
            }
        }
        return true
    }

    override fun onTouchMove(day: DateAlone): Boolean {
        val startValue = start.value
        val endInclusiveValue = endInclusive.value
        when {
            startValue == null || endInclusiveValue == null -> {
            }
            draggingStart && day.comparable > endInclusiveValue!!.comparable -> {
                start.value = endInclusive.value
                endInclusive.value = day
                draggingStart = false
                return true
            }
            !draggingStart && day.comparable < startValue!!.comparable -> {
                endInclusive.value = start.value
                start.value = day
                draggingStart = true
                return true
            }
        }

        val obs: MutableObservableProperty<DateAlone?> = if (draggingStart) start else endInclusive
        obs.value = day
        return true
    }

    override fun onTouchUp(day: DateAlone): Boolean {
        onTouchMove(day)
        return true
    }
}
