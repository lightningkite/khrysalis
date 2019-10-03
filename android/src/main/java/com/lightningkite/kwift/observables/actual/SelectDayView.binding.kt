package com.lightningkite.kwift.observables.actual

import com.lightningkite.kwift.observables.shared.MutableObservableProperty
import com.lightningkite.kwift.observables.shared.transformed
import com.lightningkite.kwift.views.android.SelectDayView
import java.util.*

fun SelectDayView.bind(day: MutableObservableProperty<Date?>) {
    this.selected = day.transformed(
        read = { it?.let { Calendar.getInstance().apply { timeInMillis = it.time } } },
        write = { it?.time }
    )
}
