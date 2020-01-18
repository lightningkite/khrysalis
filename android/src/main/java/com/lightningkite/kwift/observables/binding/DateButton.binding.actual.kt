package com.lightningkite.kwift.observables.binding

import com.lightningkite.kwift.observables.*
import com.lightningkite.kwift.rx.addWeak
import com.lightningkite.kwift.time.*
import com.lightningkite.kwift.views.android.DateButton
import com.lightningkite.kwift.views.android.TimeButton
import java.util.*


fun DateButton.bind(date: MutableObservableProperty<Date>) {
    date.observableNN.addWeak(this) { self, it ->
        self.date = it
    }
    this.onDateEntered.addWeak(this) { self, it ->
        date.value = it
    }
}

fun TimeButton.bind(date: MutableObservableProperty<Date>, minuteInterval: Int = 1) {
    this.minuteInterval = minuteInterval
    date.observableNN.addWeak(this) { self, it ->
        self.date = it
    }
    this.onDateEntered.addWeak(this) { self, it ->
        date.value = it
    }
}

fun DateButton.bindDateAlone(date: MutableObservableProperty<DateAlone>) {
    date.observableNN.addWeak(this, { self, it ->
        self.date = dateFrom(it, Date().timeAlone)
    })
    this.onDateEntered.addWeak(this) { self, it ->
        date.value = it.dateAlone
    }
}

fun TimeButton.bindTimeAlone(date: MutableObservableProperty<TimeAlone>, minuteInterval: Int = 1) {
    this.minuteInterval = minuteInterval
    date.observableNN.addWeak(this) { self, it ->
        self.date = dateFrom(Date().dateAlone, it)
    }
    this.onDateEntered.addWeak(this) { self, it ->
        date.value = it.timeAlone
    }
}
