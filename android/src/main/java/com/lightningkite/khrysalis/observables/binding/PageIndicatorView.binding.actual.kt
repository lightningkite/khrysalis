package com.lightningkite.khrysalis.observables.binding

import com.lightningkite.khrysalis.observables.*
import com.rd.PageIndicatorView

fun PageIndicatorView.bind(count: Int = 0, selected: MutableObservableProperty<Int>){
    this.count = count
    selected.addAndRunWeak(this){ self, value ->
        self.selection = value
    }
}
