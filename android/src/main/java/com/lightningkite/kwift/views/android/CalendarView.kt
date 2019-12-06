package com.lightningkite.kwift.views.android

import android.content.Context
import android.util.AttributeSet

class CalendarView : AbstractQuickCalendarView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override val ignoreDragOnDay: Boolean
        get() = false

    override fun makeChildView(): QuickMonthView {
        return QuickMonthView(context, this.childAttributeSet).also { style(it) }
    }
}
