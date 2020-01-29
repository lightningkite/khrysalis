package com.lightningkite.khrysalis.observables.binding

import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.views.android.SelectDayView
import java.util.*

fun SelectDayView.bind(day: MutableObservableProperty<Date?>) {
    this.selected = day.transformed(
        read = { it?.let { Calendar.getInstance().apply { timeInMillis = it.time } } },
        write = { it?.time }
    )
}
