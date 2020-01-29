package com.lightningkite.khrysalis.observables.binding

import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.addWeak
import com.lightningkite.khrysalis.time.*
import com.lightningkite.khrysalis.views.android.DateButton
import com.lightningkite.khrysalis.views.android.TimeButton
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
