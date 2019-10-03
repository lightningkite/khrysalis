package com.lightningkite.kwift.observables.actual

import com.lightningkite.kwift.observables.shared.MutableObservableProperty
import com.lightningkite.kwift.observables.shared.addAndRunWeak
import com.lightningkite.kwift.observables.shared.addWeak
import com.lightningkite.kwift.views.android.DateButton
import java.util.*


fun DateButton.bind(date: MutableObservableProperty<Date>) {
    date.addAndRunWeak(this) { self, it ->
        self.date = it
    }
    this.onDateEntered.addWeak(this) { self, it ->
        date.value = it
    }
}
