package com.lightningkite.kwift.observables.binding

import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.ViewFlipper
import com.lightningkite.kwift.observables.*

fun ViewFlipper.bindLoading(loading: MutableObservableProperty<Boolean>) {
    if (this.inAnimation == null)
        this.inAnimation = AlphaAnimation(0f, 1f).apply {
            duration = 250
            interpolator = LinearInterpolator()
        }
    if (this.outAnimation == null)
        this.outAnimation = AlphaAnimation(1f, 0f).apply {
            duration = 250
            interpolator = LinearInterpolator()
        }
    if (this.childCount == 1) {
        addView(ProgressBar(context), 1, FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER))
    }
    loading.addAndRunWeak(this) { self, it ->
        self.displayedChild = if (it) 1 else 0
    }
}
