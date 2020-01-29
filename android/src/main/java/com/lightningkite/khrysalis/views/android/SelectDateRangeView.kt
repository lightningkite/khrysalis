package com.lightningkite.khrysalis.views.android

import android.content.Context
import android.util.AttributeSet
import com.lightningkite.khrysalis.observables.MutableObservableProperty
import com.lightningkite.khrysalis.observables.StandardObservableProperty
import java.util.*

class SelectDateRangeView : AbstractQuickCalendarView {

    var start: MutableObservableProperty<Calendar?> = StandardObservableProperty(null)
        set(value) {
            field = value
            invalidate()
        }
    var endInclusive: MutableObservableProperty<Calendar?> = StandardObservableProperty(null)
        set(value) {
            field = value
            invalidate()
        }

    override val ignoreDragOnDay: Boolean get() = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun makeChildView(): QuickMonthView {
        return SelectDateRangeMonthView(context, childAttributeSet).apply {
            this.start = this@SelectDateRangeView.start
            this.endInclusive = this@SelectDateRangeView.endInclusive
        }.also { style(it) }
    }
}
