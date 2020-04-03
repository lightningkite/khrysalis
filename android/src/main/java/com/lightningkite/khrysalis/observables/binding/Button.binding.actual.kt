package com.lightningkite.khrysalis.observables.binding

import android.graphics.drawable.Drawable
import android.widget.Button
import com.lightningkite.khrysalis.observables.ObservableProperty
import com.lightningkite.khrysalis.views.ColorResource
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until
import com.lightningkite.khrysalis.views.backgroundDrawable

/**
 *
 * Bind isEnabled with the provided observable<Boolean>. This will turn the button on and off, or allow it to be tapped,
 * according to the value in the observable. As well you can provide a color resource for each state.
 * The colors will set the background color as it changes. By default colors don't change.
 *
 * Example
 * val active = StandardObservableProperty<Boolean>(true)
 * button.bindSelect(active, R.color.blue, R.color.grey)
 * If active is true the button can be clicked, otherwise it is not enabled, and cannot be clicked.
 * when active the button will be the blue color, otherwise the button will be grey.
 */
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


/**
 *
 * Bind Active when provided an observable<Boolean> will turn the button on and off, or allow it to be tapped,
 * according to the value in the observable. As well you can provide a drawable for each state.
 * The background drawable will change as the state does. By Default drawable doesn't change.
 *
 *  * Example
 * val active = StandardObservableProperty<Boolean>(true)
 * button.bindSelect(active, R.drawable.blue_border, R.drawable.grey_border)
 * If active is true the button can be clicked, otherwise it is not enabled, and cannot be clicked.
 * when active the button background will be the blue border, otherwise the button will be the grey border.
 */
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
