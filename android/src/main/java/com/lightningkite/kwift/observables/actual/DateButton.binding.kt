package com.lightningkite.kwift.observables.actual

import com.lightningkite.kwift.actual.*
import com.lightningkite.kwift.observables.shared.MutableObservableProperty
import com.lightningkite.kwift.observables.shared.addAndRunWeak
import com.lightningkite.kwift.observables.shared.addWeak
import com.lightningkite.kwift.views.android.DateButton
import com.lightningkite.kwift.views.android.TimeButton
import java.util.*


fun DateButton.bind(date: MutableObservableProperty<Date>) {
    date.addAndRunWeak(this) { self, it ->
        self.date = it
    }
    this.onDateEntered.addWeak(this) { self, it ->
        date.value = it
    }
}

fun TimeButton.bind(date: MutableObservableProperty<Date>) {
    date.addAndRunWeak(this) { self, it ->
        self.date = it
    }
    this.onDateEntered.addWeak(this) { self, it ->
        date.value = it
    }
}

fun DateButton.bindDateAlone(date: MutableObservableProperty<DateAlone>) {
    date.addAndRunWeak(this) { self, it ->
        self.date = dateFrom(it, Date().timeAlone)
    }
    this.onDateEntered.addWeak(this) { self, it ->
        date.value = it.dateAlone
    }
}

fun TimeButton.bindTimeAlone(date: MutableObservableProperty<TimeAlone>) {
    date.addAndRunWeak(this) { self, it ->
        self.date = dateFrom(Date().dateAlone, it)
    }
    this.onDateEntered.addWeak(this) { self, it ->
        date.value = it.timeAlone
    }
}
