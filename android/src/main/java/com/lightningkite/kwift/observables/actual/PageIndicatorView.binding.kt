package com.lightningkite.kwift.observables.actual

import com.lightningkite.kwift.observables.shared.MutableObservableProperty
import com.lightningkite.kwift.observables.shared.addAndRunWeak
import com.rd.PageIndicatorView

fun PageIndicatorView.bind(count: Int = 0, selected: MutableObservableProperty<Int>){
    this.count = count
    selected.addAndRunWeak(this){ self, value ->
        self.selection = value
    }
}
