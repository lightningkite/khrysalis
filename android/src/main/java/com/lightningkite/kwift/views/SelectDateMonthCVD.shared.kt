package com.lightningkite.kwift.views

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.DisplayMetrics
import android.view.View
import com.lightningkite.kwift.observables.*
import com.lightningkite.kwift.time.*
import java.util.*

/**Renders a swipeable calendar.**/
open class SelectDateMonthCVD : MonthCVD() {
    override fun generateAccessibilityView(): View? = null

    var selected: MutableObservableProperty<DateAlone?> = StandardObservableProperty(null)

    init {
        this.selected.onChange.addWeak(this) { self, value ->
            self.invalidate()
        }
    }

    val selectedDayPaint: Paint = Paint()
    val selectedPaint: Paint = Paint()

    override fun drawDay(
        canvas: Canvas,
        showingMonth: DateAlone,
        day: DateAlone,
        displayMetrics: DisplayMetrics,
        outer: RectF,
        inner: RectF
    ) {
        when {
            day == selected.value -> {
                CalendarDrawing.dayBackground(canvas, inner, selectedPaint)
                CalendarDrawing.day(canvas, showingMonth, day, inner, selectedDayPaint)
            }
            else -> {
                CalendarDrawing.day(canvas, showingMonth, day, inner, dayPaint)
            }
        }
    }

    override fun measure(width: Float, height: Float, displayMetrics: DisplayMetrics) {
        super.measure(width = width, height = height, displayMetrics = displayMetrics)
        selectedDayPaint.textSize = dayPaint.textSize
    }

    override fun onTap(day: DateAlone) {
        selected.value = day
    }
}
