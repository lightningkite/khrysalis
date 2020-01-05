package com.lightningkite.kwift.observables.binding

import com.lightningkite.kwift.observables.*
import com.lightningkite.kwift.views.android.SelectDayView
import java.util.*

fun SelectDayView.bind(day: MutableObservableProperty<Date?>) {
    this.selected = day.transformed(
        read = { it?.let { Calendar.getInstance().apply { timeInMillis = it.time } } },
        write = { it?.time }
    )
}
