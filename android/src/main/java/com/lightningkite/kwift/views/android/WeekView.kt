package com.lightningkite.kwift.views.android

import android.content.Context
import android.util.AttributeSet
import com.alamkanak.weekview.MonthLoader

class WeekView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : com.alamkanak.weekview.WeekView(context, attrs, defStyleAttr) {
    init {
        monthChangeListener = MonthLoader.MonthChangeListener { newYear, newMonth -> ArrayList() }
    }
}
