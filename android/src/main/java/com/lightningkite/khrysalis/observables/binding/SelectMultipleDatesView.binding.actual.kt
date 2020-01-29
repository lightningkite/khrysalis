package com.lightningkite.khrysalis.observables.binding

import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.views.android.SelectDayView
import com.lightningkite.khrysalis.views.android.SelectMultipleDatesView
import java.util.*

fun SelectMultipleDatesView.bind(dates: MutableObservableProperty<Set<Date>>) {
    this.dates = dates.transformed(
        read = { it.map { Calendar.getInstance().apply { timeInMillis = it.time } }.toSet() },
        write = { it.map { it.time }.toSet() }
    )
}
