package com.lightningkite.kwift.observables.actual

import android.widget.Button
import com.lightningkite.kwift.observables.shared.ObservableProperty
import com.lightningkite.kwift.observables.shared.addAndRunWeak
import com.lightningkite.kwift.views.actual.ColorResource


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
