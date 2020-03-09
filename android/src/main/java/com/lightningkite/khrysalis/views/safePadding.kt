package com.lightningkite.khrysalis.views

import android.view.Gravity
import android.view.View
import androidx.core.view.ViewCompat


fun View.safeInsets(gravity: Int){
    val defaultPaddingLeft = paddingLeft
    val defaultPaddingRight = paddingRight
    val defaultPaddingBottom = paddingBottom
    val defaultPaddingTop = paddingTop
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->

        println("setOnApplyWindowInsetsListener for $v with insets:  ${insets.hasSystemWindowInsets()} systemWindowInsetLeft=${insets.systemWindowInsetLeft} systemWindowInsetRight=${insets.systemWindowInsetRight} systemWindowInsetBottom=${insets.systemWindowInsetBottom} systemWindowInsetTop=${insets.systemWindowInsetTop}")

        val newPaddingLeft = if(gravity and Gravity.LEFT == Gravity.LEFT || gravity and Gravity.CENTER_HORIZONTAL == Gravity.CENTER_HORIZONTAL && gravity and Gravity.RIGHT != Gravity.RIGHT)
            insets.systemWindowInsetLeft + defaultPaddingLeft
        else
            v.paddingLeft

        val newPaddingRight = if(gravity and Gravity.RIGHT == Gravity.RIGHT || gravity and Gravity.CENTER_HORIZONTAL == Gravity.CENTER_HORIZONTAL && gravity and Gravity.LEFT != Gravity.LEFT)
            insets.systemWindowInsetRight + defaultPaddingRight
        else
            v.paddingRight

        val newPaddingBottom = if(gravity and Gravity.BOTTOM == Gravity.BOTTOM || gravity and Gravity.CENTER_VERTICAL == Gravity.CENTER_VERTICAL && gravity and Gravity.TOP != Gravity.TOP)
            insets.systemWindowInsetBottom + defaultPaddingBottom
        else
            v.paddingBottom

        val newPaddingTop = if(gravity and Gravity.TOP == Gravity.TOP || gravity and Gravity.CENTER_VERTICAL == Gravity.CENTER_VERTICAL && gravity and Gravity.BOTTOM != Gravity.BOTTOM)
            insets.systemWindowInsetTop + defaultPaddingTop
        else
            v.paddingTop

        println("Gravity: ${gravity}")
        println("Gravity.LEFT: ${gravity and Gravity.LEFT == Gravity.LEFT}")
        println("Gravity.CENTER_HORIZONTAL: ${gravity and Gravity.CENTER_HORIZONTAL == Gravity.CENTER_HORIZONTAL}")
        println("Gravity.RIGHT: ${gravity and Gravity.RIGHT == Gravity.RIGHT}")
        println("Gravity.BOTTOM: ${gravity and Gravity.BOTTOM == Gravity.BOTTOM}")
        println("Gravity.CENTER_VERTICAL: ${gravity and Gravity.CENTER_VERTICAL == Gravity.CENTER_VERTICAL}")
        println("Gravity.TOP: ${gravity and Gravity.TOP == Gravity.TOP}")
        v.setPadding(newPaddingLeft, newPaddingTop, newPaddingRight, newPaddingBottom)

        return@setOnApplyWindowInsetsListener insets
    }
    systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
}
