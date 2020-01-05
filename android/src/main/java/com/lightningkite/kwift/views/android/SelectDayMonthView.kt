package com.lightningkite.kwift.views.android

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import com.lightningkite.kwift.observables.Close
import com.lightningkite.kwift.observables.MutableObservableProperty
import com.lightningkite.kwift.observables.StandardObservableProperty
import com.lightningkite.kwift.observables.addWeak
import java.util.*

open class SelectDayMonthView : QuickMonthView {

    var selected: MutableObservableProperty<Calendar?> = StandardObservableProperty(null)
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
        closers += selected.onChange.addWeak(this) { self, value ->
            self.invalidate()
        }
    }

    override fun drawDayCell(canvas: Canvas, rectangle: RectF, day: Calendar) {
        if (day sameDay selected.value) {
            drawDayCellBackground(canvas, rectangle, selectedColorSet)
            drawDayCellText(day, canvas, rectangle, selectedColorSet)
        } else {
            drawDayCellBackground(canvas, rectangle, defaultColorSet)
            drawDayCellText(day, canvas, rectangle, defaultColorSet)
        }
    }

    private var downOn: Calendar? = null
    override fun onTouchDown(calendar: Calendar): Boolean {
        downOn = calendar
        return true
    }

    override fun onTouchMove(calendar: Calendar): Boolean {
        return calendar == downOn
    }

    override fun onTouchUp(calendar: Calendar): Boolean {
        if (downOn == calendar) {
            selected.value = calendar
        }
        return true
    }

    private infix fun Calendar?.sameDay(other: Calendar?): Boolean =
        this != null && other != null &&
                this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
                this.get(Calendar.MONTH) == other.get(Calendar.MONTH) &&
                this.get(Calendar.DAY_OF_MONTH) == other.get(Calendar.DAY_OF_MONTH)
}
