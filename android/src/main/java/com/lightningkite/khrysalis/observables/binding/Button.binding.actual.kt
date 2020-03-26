package com.lightningkite.khrysalis.observables.binding

import android.graphics.drawable.Drawable
import android.widget.Button
import com.lightningkite.khrysalis.observables.ObservableProperty
import com.lightningkite.khrysalis.views.ColorResource
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until
import com.lightningkite.khrysalis.views.backgroundDrawable


fun Button.bindActive(
    observable: ObservableProperty<Boolean>,
    activeColorResource: ColorResource? = null,
    inactiveColorResource: ColorResource? = null
) {
    observable.subscribeBy { it ->
        this.isEnabled = it
        if (it) {
            activeColorResource?.let { color ->
                this.setBackgroundResource(color)
            }
        } else {
            inactiveColorResource?.let { color ->
                this.setBackgroundResource(color)
            }
        }
    }.until(this.removed)
}

fun Button.bindActive(
    observable: ObservableProperty<Boolean>,
    activeBackground: Drawable,
    inactiveBackground: Drawable
) {
    observable.subscribeBy { it ->
        this.isEnabled = it
        if (it) {
            this.backgroundDrawable = activeBackground
        } else {

            this.backgroundDrawable = inactiveBackground
        }
    }.until(this.removed)
}
