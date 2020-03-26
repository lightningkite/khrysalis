package com.lightningkite.khrysalis.observables.binding

import android.widget.RatingBar
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until

fun RatingBar.bind(
    stars: Int,
    observable: MutableObservableProperty<Int>
) {
    this.max = stars
    this.numStars = stars
    this.incrementProgressBy(1)

    var suppress = false
    observable.subscribeBy { value ->
        if (!suppress) {
            suppress = true
            this.progress = value
            suppress = false
        }
    }.until(this.removed)
    this.onRatingBarChangeListener = object : RatingBar.OnRatingBarChangeListener {
        override fun onRatingChanged(p0: RatingBar, p1: Float, p2: Boolean) {
            if (!suppress) {
                suppress = true
                observable.value = p0.progress
                suppress = false
            }
        }
    }

}

fun RatingBar.bind(
    stars: Int,
    observable: ObservableProperty<Int>
) {
    this.max = stars
    this.numStars = stars
    this.setIsIndicator(true)

    observable.subscribeBy { value ->
        this.progress = value
    }.until(this.removed)
}

fun RatingBar.bindFloat(
    stars: Int,
    observable: MutableObservableProperty<Float>
) {
    this.numStars = stars
    this.stepSize = 0.01f

    var suppress = false
    observable.subscribeBy { value ->
        if (!suppress) {
            suppress = true
            this.rating = value
            suppress = false
        }
    }.until(this.removed)
    this.onRatingBarChangeListener = object : RatingBar.OnRatingBarChangeListener {
        override fun onRatingChanged(p0: RatingBar, p1: Float, p2: Boolean) {
            if (!suppress) {
                suppress = true
                observable.value = p1
                suppress = false
            }
        }
    }

}

fun RatingBar.bindFloat(
    stars: Int,
    observable: ObservableProperty<Float>
) {
    this.numStars = stars
    this.setIsIndicator(true)

    observable.subscribeBy { value ->
        this.rating = value
    }.until(this.removed)
}
