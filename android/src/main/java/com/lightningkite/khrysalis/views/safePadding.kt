package com.lightningkite.khrysalis.views

import android.app.Activity
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lightningkite.khrysalis.views.android.activity
import java.util.*

object SafePaddingFlags {
    const val NONE: Int = 0
    const val TOP: Int = 1
    const val RIGHT: Int = 2
    const val BOTTOM: Int = 4
    const val LEFT: Int = 8
    const val HORIZONTAL: Int = 10
    const val VERTICAL: Int = 5
    const val ALL: Int = 15
}

fun View.safeInsets(flags: Int) {
    val defaultPaddingLeft = paddingLeft
    val defaultPaddingRight = paddingRight
    val defaultPaddingBottom = paddingBottom
    val defaultPaddingTop = paddingTop
    val useLeft = flags and SafePaddingFlags.LEFT != 0
    val useRight = flags and SafePaddingFlags.RIGHT != 0
    val useBottom = flags and SafePaddingFlags.BOTTOM != 0
    val useTop = flags and SafePaddingFlags.TOP != 0

    ViewCompat.setOnApplyWindowInsetsListener(this) label@{ v: View, insets: WindowInsetsCompat ->

        val newPaddingLeft = if (useLeft)
            insets.systemWindowInsetLeft + defaultPaddingLeft
        else
            v.paddingLeft

        val newPaddingRight = if (useRight)
            insets.systemWindowInsetRight + defaultPaddingRight
        else
            v.paddingRight

        val newPaddingBottom = if (useBottom)
            insets.systemWindowInsetBottom + defaultPaddingBottom
        else
            v.paddingBottom

        val newPaddingTop = if (useTop)
            insets.systemWindowInsetTop + defaultPaddingTop
        else
            v.paddingTop

        v.setPadding(newPaddingLeft, newPaddingTop, newPaddingRight, newPaddingBottom)
        v.requestLayout()

        return@label insets.replaceSystemWindowInsets(
            if (useLeft) 0 else insets.systemWindowInsetLeft,
            if (useTop) 0 else insets.systemWindowInsetTop,
            if (useRight) 0 else insets.systemWindowInsetRight,
            if (useBottom) 0 else insets.systemWindowInsetBottom
        )
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
            }

            override fun onViewAttachedToWindow(v: View?) {
                v?.resetWindowInsets()
            }
        })
    }
    systemUiVisibility =
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
}

private val resetWindowInsetsPending = HashSet<View>()
fun View.resetWindowInsets() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        (this.parent as? ViewGroup)?.resetWindowInsets() ?: run {
            if(resetWindowInsetsPending.add(this)){
                this.post {
                    this.rootWindowInsets?.let { startInsets ->
                        dispatchApplyWindowInsets(startInsets)
                    }
                    resetWindowInsetsPending.remove(this)
                }
            }
        }
    }
}

fun View.safeInsetsSizing(flags: Int) {
    val defaultWidth = this.layoutParams.width
    val defaultHeight = this.layoutParams.height
    val useLeft = flags and SafePaddingFlags.LEFT != 0
    val useRight = flags and SafePaddingFlags.RIGHT != 0
    val useBottom = flags and SafePaddingFlags.BOTTOM != 0
    val useTop = flags and SafePaddingFlags.TOP != 0
    ViewCompat.setOnApplyWindowInsetsListener(this) label@{ v: View, insets: WindowInsetsCompat ->

        var newWidth = defaultWidth
        var newHeight = defaultHeight

        if (useLeft)
            newWidth += insets.systemWindowInsetLeft

        if (useRight)
            newWidth += insets.systemWindowInsetRight

        if (useBottom)
            newHeight += insets.systemWindowInsetBottom

        if (useTop)
            newHeight += insets.systemWindowInsetTop

        v.layoutParams.width = newWidth
        v.layoutParams.height = newHeight
        v.parent.requestLayout()

        return@label insets.replaceSystemWindowInsets(
            if (useLeft) 0 else insets.systemWindowInsetLeft,
            if (useTop) 0 else insets.systemWindowInsetTop,
            if (useRight) 0 else insets.systemWindowInsetRight,
            if (useBottom) 0 else insets.systemWindowInsetBottom
        )
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
            }

            override fun onViewAttachedToWindow(v: View?) {
                v?.resetWindowInsets()
            }
        })
    }
    systemUiVisibility =
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
}


fun View.safeInsetsBoth(flags: Int) {
    val defaultPaddingLeft = paddingLeft
    val defaultPaddingRight = paddingRight
    val defaultPaddingBottom = paddingBottom
    val defaultPaddingTop = paddingTop
    val defaultWidth = this.layoutParams.width
    val defaultHeight = this.layoutParams.height
    val useLeft = flags and SafePaddingFlags.LEFT != 0
    val useRight = flags and SafePaddingFlags.RIGHT != 0
    val useBottom = flags and SafePaddingFlags.BOTTOM != 0
    val useTop = flags and SafePaddingFlags.TOP != 0

    ViewCompat.setOnApplyWindowInsetsListener(this) label@{ v: View, insets: WindowInsetsCompat ->

        var newWidth = defaultWidth
        var newHeight = defaultHeight

        if (useLeft)
            newWidth += insets.systemWindowInsetLeft

        if (useRight)
            newWidth += insets.systemWindowInsetRight

        if (useBottom)
            newHeight += insets.systemWindowInsetBottom

        if (useTop)
            newHeight += insets.systemWindowInsetTop

        v.layoutParams.width = newWidth
        v.layoutParams.height = newHeight

        val newPaddingLeft = if (useLeft)
            insets.systemWindowInsetLeft + defaultPaddingLeft
        else
            v.paddingLeft

        val newPaddingRight = if (useRight)
            insets.systemWindowInsetRight + defaultPaddingRight
        else
            v.paddingRight

        val newPaddingBottom = if (useBottom)
            insets.systemWindowInsetBottom + defaultPaddingBottom
        else
            v.paddingBottom

        val newPaddingTop = if (useTop)
            insets.systemWindowInsetTop + defaultPaddingTop
        else
            v.paddingTop

        v.setPadding(newPaddingLeft, newPaddingTop, newPaddingRight, newPaddingBottom)
        v.parent.requestLayout()

        return@label insets.replaceSystemWindowInsets(
            if (useLeft) 0 else insets.systemWindowInsetLeft,
            if (useTop) 0 else insets.systemWindowInsetTop,
            if (useRight) 0 else insets.systemWindowInsetRight,
            if (useBottom) 0 else insets.systemWindowInsetBottom
        )
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
            }

            override fun onViewAttachedToWindow(v: View?) {
                v?.resetWindowInsets()
            }
        })
    }
    systemUiVisibility =
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
}
