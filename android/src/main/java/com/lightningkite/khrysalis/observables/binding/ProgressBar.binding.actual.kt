package com.lightningkite.khrysalis.observables.binding

import android.widget.ProgressBar
import com.lightningkite.khrysalis.observables.MutableObservableProperty
import com.lightningkite.khrysalis.observables.ObservableProperty
import com.lightningkite.khrysalis.observables.subscribeBy
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until

fun ProgressBar.bindInt(
    observable: ObservableProperty<Int>
){
    observable.subscribeBy { value ->
        this.progress = value
    }.until(this@bindInt.removed)
}
fun ProgressBar.bindFloat(
    observable: ObservableProperty<Float>
){
    observable.subscribeBy { value ->
        this.progress = (value.coerceIn(0.0f, 1.0f) * 100).toInt()
    }.until(this@bindFloat.removed)
}