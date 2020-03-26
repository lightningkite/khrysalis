package com.lightningkite.khrysalis.observables.binding

import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until
import com.lightningkite.khrysalis.time.*
import com.lightningkite.khrysalis.views.android.DateButton
import com.lightningkite.khrysalis.views.android.TimeButton
import java.util.*


fun DateButton.bind(date: MutableObservableProperty<Date>) {
    date.subscribeBy { it ->
        this.date = it

    }.until(this.removed)
    this.onDateEntered.subscribe { it ->
        date.value = it
    }.until(this.removed)
}

fun TimeButton.bind(date: MutableObservableProperty<Date>, minuteInterval: Int = 1) {
    this.minuteInterval = minuteInterval
    date.subscribeBy { it ->
        this.date = it
    }.until(this.removed)
    this.onDateEntered.subscribe { it ->
        date.value = it
    }.until(this.removed)
}

fun DateButton.bindDateAlone(date: MutableObservableProperty<DateAlone>) {
    date.subscribeBy { it ->
        this.date = dateFrom(it, Date().timeAlone)
    }.until(this.removed)
    this.onDateEntered.subscribe { it ->
        date.value = it.dateAlone
    }.until(this.removed)
}

fun TimeButton.bindTimeAlone(date: MutableObservableProperty<TimeAlone>, minuteInterval: Int = 1) {
    this.minuteInterval = minuteInterval
    date.subscribeBy { it ->
        this.date = dateFrom(Date().dateAlone, it)
    }.until(this.removed)
    this.onDateEntered.subscribe { it ->
        date.value = it.timeAlone
    }.until(this.removed)
}
