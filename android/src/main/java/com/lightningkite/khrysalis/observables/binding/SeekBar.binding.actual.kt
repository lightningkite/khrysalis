package com.lightningkite.khrysalis.observables.binding

import android.widget.SeekBar
import com.lightningkite.khrysalis.observables.*

fun SeekBar.bind(
    start: Int,
    endInclusive: Int,
    observable: MutableObservableProperty<Int>
) {
    this.max = endInclusive - start
    this.incrementProgressBy(1)

    var suppress = false
    observable.addAndRunWeak(this) { self, value ->
        if (!suppress) {
            suppress = true
            self.progress = value + start
            suppress = false
        }
    }
    this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            if (!suppress) {
                suppress = true
                observable.value = p1
                suppress = false
            }
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
        }
    })

}
