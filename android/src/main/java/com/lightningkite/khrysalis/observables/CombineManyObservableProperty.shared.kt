package com.lightningkite.khrysalis.observables

import com.lightningkite.khrysalis.Box
import com.lightningkite.khrysalis.JsName
import com.lightningkite.khrysalis.boxWrap
import com.lightningkite.khrysalis.escaping
import com.lightningkite.khrysalis.rx.combineLatest
import io.reactivex.Observable

class CombineManyObservableProperty<IN>(
    val observables: List<ObservableProperty<IN>>
): ObservableProperty<List<IN>>() {
    override val value: List<IN>
        get() = observables.map { it.value }
    override val onChange: Observable<Box<List<IN>>>
        get() = observables.map { it.observable }.combineLatest { items ->
            Box.wrap(items.map { it.value })
        }.skip(1)
}

@JsName("combinedAndMap")
fun <IN, OUT> List<ObservableProperty<IN>>.combined(
    combiner: @escaping() (List<IN>) -> OUT
): ObservableProperty<OUT> {
    return CombineManyObservableProperty(this).map(combiner)
}


fun <IN> List<ObservableProperty<IN>>.combined(): ObservableProperty<List<IN>> {
    return CombineManyObservableProperty(this)
}
