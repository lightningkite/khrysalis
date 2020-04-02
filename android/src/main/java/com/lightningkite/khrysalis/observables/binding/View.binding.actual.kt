package com.lightningkite.khrysalis.observables.binding

import android.view.View
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until

/**
 *
 * Binds the visibility of the view to the observable provided.
 * If the value if false, the view will not be visible, nor intractable, though the
 * view will still take up space.
 *
 */

fun View.bindVisible(observable: ObservableProperty<Boolean>) {
    observable.subscribeBy { value ->
        this.visibility = if (value) View.VISIBLE else View.INVISIBLE
    }.until(this.removed)
}


/**
 *
 * Binds the existance of the view to the observable provided.
 * If the value if false, the view will not exist, meaning it will not be visible,
 * it will not be intractable, and it will not take up any space.
 *
 */
fun View.bindExists(observable: ObservableProperty<Boolean>) {
    observable.subscribeBy { value ->
        this.visibility = if (value) View.VISIBLE else View.GONE
    }.until(this.removed)
}
