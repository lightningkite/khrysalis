package com.lightningkite.kwift.observables.binding

import android.widget.RatingBar
import com.lightningkite.kwift.observables.*

fun RatingBar.bind(
    stars: Int,
    observable: MutableObservableProperty<Int>
) {
    this.max = stars
    this.numStars = stars
    this.incrementProgressBy(1)

    var suppress = false
    observable.addAndRunWeak(this) { self, value ->
        if (!suppress) {
            suppress = true
            self.progress = value
            suppress = false
        }
    }
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

    observable.addAndRunWeak(this) { self, value ->
        self.progress = value
    }
}

fun RatingBar.bindFloat(
    stars: Int,
    observable: MutableObservableProperty<Float>
) {
    this.numStars = stars
    this.stepSize = 0.01f

    var suppress = false
    observable.addAndRunWeak(this) { self, value ->
        if (!suppress) {
            suppress = true
            self.rating = value
            suppress = false
        }
    }
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

    observable.addAndRunWeak(this) { self, value ->
        self.rating = value
    }
}
