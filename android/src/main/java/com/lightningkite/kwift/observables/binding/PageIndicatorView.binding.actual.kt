package com.lightningkite.kwift.observables.binding

import com.lightningkite.kwift.observables.*
import com.rd.PageIndicatorView

fun PageIndicatorView.bind(count: Int = 0, selected: MutableObservableProperty<Int>){
    this.count = count
    selected.addAndRunWeak(this){ self, value ->
        self.selection = value
    }
}
