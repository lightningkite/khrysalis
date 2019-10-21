package com.lightningkite.kwift.observables.actual

import com.lightningkite.kwift.observables.shared.MutableObservableProperty
import com.lightningkite.kwift.observables.shared.transformed
import com.lightningkite.kwift.views.android.SelectDayView
import com.lightningkite.kwift.views.android.SelectMultipleDatesView
import java.util.*

fun SelectMultipleDatesView.bind(dates: MutableObservableProperty<Set<Date>>) {
    this.dates = dates.transformed(
        read = { it.map { Calendar.getInstance().apply { timeInMillis = it.time } }.toSet() },
        write = { it.map { it.time }.toSet() }
    )
}
