package com.lightningkite.khrysalis.observables.binding

import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.views.android.SelectDateRangeView
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
