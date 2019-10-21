package com.lightningkite.kwift.views.android

import android.content.Context
import android.util.AttributeSet
import com.lightningkite.kwift.observables.shared.MutableObservableProperty
import com.lightningkite.kwift.observables.shared.StandardObservableProperty
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
        }
    }
}
