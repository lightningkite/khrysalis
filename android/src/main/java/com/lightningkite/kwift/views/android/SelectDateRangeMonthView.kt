package com.lightningkite.kwift.views.android

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import com.lightningkite.kwift.observables.shared.Close
import com.lightningkite.kwift.observables.shared.MutableObservableProperty
import com.lightningkite.kwift.observables.shared.StandardObservableProperty
import com.lightningkite.kwift.observables.shared.addWeak
import java.util.*

open class SelectDateRangeMonthView : QuickMonthView {

    var draggingStart = true
    var start: MutableObservableProperty<Calendar?> = StandardObservableProperty(null)
        set(value) {
            field = value
            init()
            invalidate()
        }
    var endInclusive: MutableObservableProperty<Calendar?> = StandardObservableProperty(null)
        set(value) {
            field = value
            init()
            invalidate()
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    val closers: ArrayList<Close> = ArrayList()
    fun init() {
        for (close in closers) {
            close.close()
        }
        closers += start.onChange.addWeak(this) { self, value ->
            self.invalidate()
        }
        closers += endInclusive.onChange.addWeak(this) { self, value ->
            self.invalidate()
        }
    }

    override fun drawDayCell(canvas: Canvas, rectangle: RectF, day: Calendar) {
        when {
            day sameDay start.value && (day sameDay endInclusive.value || endInclusive.value == null) -> {
                drawDayCellBackground(canvas, rectangle, selectedColorSet)
                drawDayCellText(day, canvas, rectangle, selectedColorSet)
            }
            day sameDay start.value -> {
                drawDayCellBackgroundStart(canvas, rectangle, selectedColorSet)
                drawDayCellText(day, canvas, rectangle, selectedColorSet)
            }
            day sameDay endInclusive.value -> {
                drawDayCellBackgroundEnd(canvas, rectangle, selectedColorSet)
                drawDayCellText(day, canvas, rectangle, selectedColorSet)
            }
            day.timeInMillis > start.value?.timeInMillis ?: Long.MAX_VALUE && day.timeInMillis < endInclusive.value?.timeInMillis ?: Long.MIN_VALUE -> {
                drawDayCellBackgroundMiddle(canvas, rectangle, selectedColorSet)
                drawDayCellText(day, canvas, rectangle, selectedColorSet)
            }
            else -> {
                drawDayCellBackground(canvas, rectangle, defaultColorSet)
                drawDayCellText(day, canvas, rectangle, defaultColorSet)
            }
        }
    }

    private var startedDraggingOn: Calendar? = null
    private var everMoved = false

    override fun onTouchDown(calendar: Calendar): Boolean {
        startedDraggingOn = calendar
        everMoved = false
        //If on start/end - drag
        //If after, extend
        //If before, extend
        //If middle, collapse all
        val startValue = start.value
        val endInclusiveValue = endInclusive.value

        when {
            startValue == null || endInclusiveValue == null -> {
                start.value = calendar
                endInclusive.value = calendar
                draggingStart = false
            }
            calendar sameDay endInclusiveValue -> {
                draggingStart = false
            }
            calendar sameDay startValue -> {
                draggingStart = true
            }
            calendar.after(endInclusiveValue) && startValue sameDay endInclusiveValue -> {
                endInclusive.value = calendar
                draggingStart = false
            }
            else -> {
                start.value = calendar
                endInclusive.value = calendar
                draggingStart = false
            }
        }
        return true
    }

    override fun onTouchMove(calendar: Calendar): Boolean {

        if(!(calendar sameDay startedDraggingOn)){
            everMoved = true
        }
        val startValue = start.value
        val endInclusiveValue = endInclusive.value
        when {
            startValue == null || endInclusiveValue == null -> {
            }
            draggingStart && calendar.after(endInclusiveValue) -> {
                start.value = endInclusive.value
                endInclusive.value = calendar
                draggingStart = false
                return true
            }
            !draggingStart && calendar.before(startValue) -> {
                endInclusive.value = start.value
                start.value = calendar
                draggingStart = true
                return true
            }
        }

        val obs = if (draggingStart) start else endInclusive
        obs.value = calendar
        return true
    }

    override fun onTouchUp(calendar: Calendar): Boolean {
        onTouchMove(calendar)
        if(startedDraggingOn sameDay calendar && !everMoved){
            start.value = calendar
            endInclusive.value = calendar
        }
        return true
    }

    private infix fun Calendar?.sameDay(other: Calendar?): Boolean =
        this != null && other != null &&
                this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
                this.get(Calendar.MONTH) == other.get(Calendar.MONTH) &&
                this.get(Calendar.DAY_OF_MONTH) == other.get(Calendar.DAY_OF_MONTH)
}
