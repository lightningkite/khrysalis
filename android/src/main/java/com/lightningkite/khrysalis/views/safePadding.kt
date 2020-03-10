package com.lightningkite.khrysalis.views

import android.app.Activity
import android.os.Build
import android.view.Gravity
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


fun View.safeInsets(gravity: Int){
    val defaultPaddingLeft = paddingLeft
    val defaultPaddingRight = paddingRight
    val defaultPaddingBottom = paddingBottom
    val defaultPaddingTop = paddingTop
    ViewCompat.setOnApplyWindowInsetsListener(this) label@{ v: View, insets: WindowInsetsCompat ->

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

        return@label insets
    }
    post {
        val v = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (context as? Activity)?.window?.decorView?.rootWindowInsets?.let { insets ->
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
            }
        }
    }
    systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
}

fun View.safeInsetsSizing(gravity: Int){
    val defaultWidth = this.layoutParams.width
    val defaultHeight = this.layoutParams.height
    ViewCompat.setOnApplyWindowInsetsListener(this) label@{ v: View, insets: WindowInsetsCompat ->

        println("setOnApplyWindowInsetsListener for $v with insets:  ${insets.hasSystemWindowInsets()} systemWindowInsetLeft=${insets.systemWindowInsetLeft} systemWindowInsetRight=${insets.systemWindowInsetRight} systemWindowInsetBottom=${insets.systemWindowInsetBottom} systemWindowInsetTop=${insets.systemWindowInsetTop}")

        var newWidth = defaultWidth
        var newHeight = defaultHeight

        if(gravity and Gravity.LEFT == Gravity.LEFT || gravity and Gravity.CENTER_HORIZONTAL == Gravity.CENTER_HORIZONTAL && gravity and Gravity.RIGHT != Gravity.RIGHT)
            newWidth += insets.systemWindowInsetLeft

        if(gravity and Gravity.RIGHT == Gravity.RIGHT || gravity and Gravity.CENTER_HORIZONTAL == Gravity.CENTER_HORIZONTAL && gravity and Gravity.LEFT != Gravity.LEFT)
            newWidth += insets.systemWindowInsetRight

        if(gravity and Gravity.BOTTOM == Gravity.BOTTOM || gravity and Gravity.CENTER_VERTICAL == Gravity.CENTER_VERTICAL && gravity and Gravity.TOP != Gravity.TOP)
            newHeight += insets.systemWindowInsetBottom

        if(gravity and Gravity.TOP == Gravity.TOP || gravity and Gravity.CENTER_VERTICAL == Gravity.CENTER_VERTICAL && gravity and Gravity.BOTTOM != Gravity.BOTTOM)
            newHeight += insets.systemWindowInsetTop

        println("Gravity: ${gravity}")
        println("Gravity.LEFT: ${gravity and Gravity.LEFT == Gravity.LEFT}")
        println("Gravity.CENTER_HORIZONTAL: ${gravity and Gravity.CENTER_HORIZONTAL == Gravity.CENTER_HORIZONTAL}")
        println("Gravity.RIGHT: ${gravity and Gravity.RIGHT == Gravity.RIGHT}")
        println("Gravity.BOTTOM: ${gravity and Gravity.BOTTOM == Gravity.BOTTOM}")
        println("Gravity.CENTER_VERTICAL: ${gravity and Gravity.CENTER_VERTICAL == Gravity.CENTER_VERTICAL}")
        println("Gravity.TOP: ${gravity and Gravity.TOP == Gravity.TOP}")
        v.layoutParams.width = newWidth
        v.layoutParams.height = newHeight

        return@label insets
    }
    post {
        val v = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (context as? Activity)?.window?.decorView?.rootWindowInsets?.let { insets ->
                println("setOnApplyWindowInsetsListener for $v with insets:  ${insets.hasSystemWindowInsets()} systemWindowInsetLeft=${insets.systemWindowInsetLeft} systemWindowInsetRight=${insets.systemWindowInsetRight} systemWindowInsetBottom=${insets.systemWindowInsetBottom} systemWindowInsetTop=${insets.systemWindowInsetTop}")

                var newWidth = defaultWidth
                var newHeight = defaultHeight

                if(gravity and Gravity.LEFT == Gravity.LEFT || gravity and Gravity.CENTER_HORIZONTAL == Gravity.CENTER_HORIZONTAL && gravity and Gravity.RIGHT != Gravity.RIGHT)
                    newWidth += insets.systemWindowInsetLeft

                if(gravity and Gravity.RIGHT == Gravity.RIGHT || gravity and Gravity.CENTER_HORIZONTAL == Gravity.CENTER_HORIZONTAL && gravity and Gravity.LEFT != Gravity.LEFT)
                    newWidth += insets.systemWindowInsetRight

                if(gravity and Gravity.BOTTOM == Gravity.BOTTOM || gravity and Gravity.CENTER_VERTICAL == Gravity.CENTER_VERTICAL && gravity and Gravity.TOP != Gravity.TOP)
                    newHeight += insets.systemWindowInsetBottom

                if(gravity and Gravity.TOP == Gravity.TOP || gravity and Gravity.CENTER_VERTICAL == Gravity.CENTER_VERTICAL && gravity and Gravity.BOTTOM != Gravity.BOTTOM)
                    newHeight += insets.systemWindowInsetTop

                println("Gravity: ${gravity}")
                println("Gravity.LEFT: ${gravity and Gravity.LEFT == Gravity.LEFT}")
                println("Gravity.CENTER_HORIZONTAL: ${gravity and Gravity.CENTER_HORIZONTAL == Gravity.CENTER_HORIZONTAL}")
                println("Gravity.RIGHT: ${gravity and Gravity.RIGHT == Gravity.RIGHT}")
                println("Gravity.BOTTOM: ${gravity and Gravity.BOTTOM == Gravity.BOTTOM}")
                println("Gravity.CENTER_VERTICAL: ${gravity and Gravity.CENTER_VERTICAL == Gravity.CENTER_VERTICAL}")
                println("Gravity.TOP: ${gravity and Gravity.TOP == Gravity.TOP}")
                v.layoutParams.width = newWidth
                v.layoutParams.height = newHeight
            }
        }
    }
    systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
}
