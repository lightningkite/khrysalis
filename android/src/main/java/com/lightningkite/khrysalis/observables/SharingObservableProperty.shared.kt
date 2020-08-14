package com.lightningkite.khrysalis.observables

import com.lightningkite.khrysalis.Box
import com.lightningkite.khrysalis.WeakSelf
import io.reactivex.Observable

class SharingObservableProperty<T>(
    val basedOn: ObservableProperty<T>,
    val startAsListening: Boolean = false
) : ObservableProperty<T>() {
    var cachedValue: T = basedOn.value
    var isListening = startAsListening
    override val value: T
        get() = if (isListening) cachedValue else basedOn.value

    override val onChange: Observable<Box<T>> = basedOn.onChange
        .doOnNext @WeakSelf { this?.cachedValue = it.value }
        .doOnSubscribe @WeakSelf { this?.isListening = true }
        .doOnDispose @WeakSelf { this?.isListening = false }
        .share()
}

fun <T> ObservableProperty<T>.share(startAsListening: Boolean = false): SharingObservableProperty<T> {
    return SharingObservableProperty<T>(this, startAsListening)
}