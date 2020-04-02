package com.lightningkite.khrysalis.observables.binding

import android.widget.SeekBar
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until

/**
 *
 * Binds the value of the seek bar to the observable. You can also state what you want the
 * low and high values the seek bar can have. Any change to the seek bar will set the observable
 * as well any changes in the observable will manifest in the seek bar.
 *
 */

fun SeekBar.bind(
    start: Int,
    endInclusive: Int,
    observable: MutableObservableProperty<Int>
) {
    this.max = endInclusive - start
    this.incrementProgressBy(1)

    var suppress = false
    observable.subscribeBy { value ->
        if (!suppress) {
            suppress = true
            this.progress = value + start
            suppress = false
        }
    }.until(this@bind.removed)
    this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            if (!suppress) {
                suppress = true
                observable.value = p1 + start
                suppress = false
            }
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
        }
    })

}
