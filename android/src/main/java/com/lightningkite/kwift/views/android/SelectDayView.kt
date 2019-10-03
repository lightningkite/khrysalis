package com.lightningkite.kwift.views.android

import android.content.Context
import android.util.AttributeSet
import com.lightningkite.kwift.observables.shared.MutableObservableProperty
import com.lightningkite.kwift.observables.shared.StandardObservableProperty
import java.util.*

class SelectDayView : AbstractQuickCalendarView {

    var selected: MutableObservableProperty<Calendar?> = StandardObservableProperty(null)
        set(value) {
            field = value
            invalidate()
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override val ignoreDragOnDay: Boolean
        get() = false

    override fun makeChildView(): QuickMonthView {
        return SelectDayMonthView(context, childAttributeSet).apply {
            this.selected = this@SelectDayView.selected
        }
    }
}
