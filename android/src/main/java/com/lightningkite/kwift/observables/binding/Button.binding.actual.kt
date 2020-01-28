package com.lightningkite.kwift.observables.binding

import android.graphics.drawable.Drawable
import android.widget.Button
import com.lightningkite.kwift.observables.ObservableProperty
import com.lightningkite.kwift.observables.addAndRunWeak
import com.lightningkite.kwift.views.ColorResource
import com.lightningkite.kwift.observables.*
import com.lightningkite.kwift.views.backgroundDrawable


fun Button.bindActive(
    observable: ObservableProperty<Boolean>,
    activeColorResource: ColorResource? = null,
    inactiveColorResource: ColorResource? = null
) {
    observable.addAndRunWeak(this) { self, it ->
        self.isEnabled = it
        if (it) {
            activeColorResource?.let { color ->
                self.setBackgroundResource(color)
            }
        } else {
            inactiveColorResource?.let { color ->
                self.setBackgroundResource(color)
            }
        }
    }
}

fun Button.bindActive(
    observable: ObservableProperty<Boolean>,
    activeBackground: Drawable,
    inactiveBackground: Drawable
) {
    observable.addAndRunWeak(this) { self, it ->
        self.isEnabled = it
        if (it) {
            self.backgroundDrawable = activeBackground
        } else {

            self.backgroundDrawable = inactiveBackground
        }
    }
}