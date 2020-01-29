package com.lightningkite.khrysalis.views.android

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import com.lightningkite.khrysalis.observables.Close
import com.lightningkite.khrysalis.observables.MutableObservableProperty
import com.lightningkite.khrysalis.observables.StandardObservableProperty
import com.lightningkite.khrysalis.rx.addWeak
import io.reactivex.disposables.Disposable
import java.util.*

open class SelectMultipleDatesMonthView : QuickMonthView {

    var dates: MutableObservableProperty<Set<Calendar>> = StandardObservableProperty(setOf())
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

    val closers: ArrayList<Disposable> = ArrayList()
    fun init() {
        for (close in closers) {
            close.dispose()
        }
        closers += dates.onChange.addWeak(this) { self, value ->
            self.invalidate()
        }
    }

    override fun drawDayCell(canvas: Canvas, rectangle: RectF, day: Calendar) {
        if (dates.value.any { it -> day.sameDay(it) }) {
            val leftCal = (day.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, -1) }
            val left = dates.value.any { it -> leftCal.sameDay(it) }
            val rightCal = (day.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 1) }
            val right = dates.value.any { it -> rightCal.sameDay(it) }

            when {
                !left && !right -> {
                    drawDayCellBackground(canvas, rectangle, selectedColorSet)
                    drawDayCellText(day, canvas, rectangle, selectedColorSet)
                }
                !left && right -> {
                    drawDayCellBackgroundStart(canvas, rectangle, selectedColorSet)
                    drawDayCellText(day, canvas, rectangle, selectedColorSet)
                }
                left && !right -> {
                    drawDayCellBackgroundEnd(canvas, rectangle, selectedColorSet)
                    drawDayCellText(day, canvas, rectangle, selectedColorSet)
                }
                left && right -> {
                    drawDayCellBackgroundMiddle(canvas, rectangle, selectedColorSet)
                    drawDayCellText(day, canvas, rectangle, selectedColorSet)
                }
                else -> {
                    drawDayCellBackground(canvas, rectangle, defaultColorSet)
                    drawDayCellText(day, canvas, rectangle, defaultColorSet)
                }
            }
            drawDayCellBackground(canvas, rectangle, selectedColorSet)
            drawDayCellText(day, canvas, rectangle, selectedColorSet)
        } else {
            drawDayCellBackground(canvas, rectangle, defaultColorSet)
            drawDayCellText(day, canvas, rectangle, defaultColorSet)
        }
    }

    var adding = false
    override fun onTouchDown(calendar: Calendar): Boolean {
        adding = dates.value.none { it -> calendar.sameDay(it) }
        onTouchMove(calendar)
        return true
    }

    override fun onTouchMove(calendar: Calendar): Boolean {
        if (adding) {
            if (dates.value.none { it -> calendar.sameDay(it) }) {
                dates.value = dates.value.plus(calendar)
            }
        } else {
            dates.value = dates.value.filter { !(it sameDay calendar) }.toSet()
        }
        return true
    }

    override fun onTouchUp(calendar: Calendar): Boolean {
        return true
    }

    private infix fun Calendar?.sameDay(other: Calendar?): Boolean =
        this != null && other != null &&
                this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
                this.get(Calendar.MONTH) == other.get(Calendar.MONTH) &&
                this.get(Calendar.DAY_OF_MONTH) == other.get(Calendar.DAY_OF_MONTH)
}
