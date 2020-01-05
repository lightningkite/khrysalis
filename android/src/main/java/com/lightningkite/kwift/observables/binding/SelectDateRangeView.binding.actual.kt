package com.lightningkite.kwift.observables.binding

import com.lightningkite.kwift.observables.*
import com.lightningkite.kwift.views.android.SelectDateRangeView
import java.util.*

fun SelectDateRangeView.bind(start: MutableObservableProperty<Date?>, endInclusive: MutableObservableProperty<Date?>) {
    this.start = start.transformed(
        read = { it?.let { Calendar.getInstance().apply { timeInMillis = it.time } } },
        write = { it?.time }
    )
    this.endInclusive = endInclusive.transformed(
        read = { it?.let { Calendar.getInstance().apply { timeInMillis = it.time } } },
        write = { it?.time }
    )
}
