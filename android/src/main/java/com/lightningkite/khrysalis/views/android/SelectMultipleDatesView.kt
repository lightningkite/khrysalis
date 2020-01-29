package com.lightningkite.khrysalis.views.android

import android.content.Context
import android.util.AttributeSet
import com.lightningkite.khrysalis.observables.MutableObservableProperty
import com.lightningkite.khrysalis.observables.StandardObservableProperty
import java.util.*

class SelectMultipleDatesView : AbstractQuickCalendarView {

    var dates: MutableObservableProperty<Set<Calendar>> = StandardObservableProperty(setOf())
        set(value) {
            field = value
            invalidate()
        }

    override val ignoreDragOnDay: Boolean get() = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun makeChildView(): QuickMonthView {
        return SelectMultipleDatesMonthView(context, childAttributeSet).apply {
            this.dates = this@SelectMultipleDatesView.dates
        }.also { style(it) }
    }
}
