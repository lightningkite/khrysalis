@file:Suppress("NAME_SHADOWING")

package com.lightningkite.khrysalis.observables.binding

import android.graphics.LightingColorFilter
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.ViewFlipper
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until
import com.lightningkite.khrysalis.views.ColorResource

/**
 *
 * bindLoading will flip the view between any built in views, and the native android loading animation.
 * If the value in loading is false it will display whatever view it normally holds.
 * If the value in loading is true it will hide any views it holds and display the loading animation.
 * Color will set the color of the loading animation to whatever resource is provided.
 *
 */

fun ViewFlipper.bindLoading(loading: ObservableProperty<Boolean>, color: ColorResource? = null) {
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
        val spinner = ProgressBar(context)
        color?.let{ color ->
            //TODO: Make this actually show the proper color. Currently it only makes it white.
            val colorValue = resources.getColor(color)
            spinner.indeterminateDrawable.colorFilter = LightingColorFilter( 0xFFFFFFFF.toInt() - colorValue, colorValue)
        }
        addView(spinner, 1, FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER))
    }
    loading.subscribeBy { it ->
        this.displayedChild = if (it) 1 else 0
    }.until(this.removed)
}
