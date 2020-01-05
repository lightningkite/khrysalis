package com.lightningkite.kwift.views

import android.view.View
import android.graphics.drawable.Drawable
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

var View.backgroundDrawable: Drawable?
    get() = this.background
    set(value) {
        this.background = value
    }

var View.backgroundResource: Int
    get() = 0
    set(value) {
        this.setBackgroundResource(value)
    }


fun View.onClick(action: () -> Unit) {
    onClick(500, action)
}

fun View.onClick(disabledMilliseconds: Long, action: () -> Unit) {
    var lastActivated = System.currentTimeMillis()
    setOnClickListener {
        if(System.currentTimeMillis() - lastActivated > disabledMilliseconds) {
            action()
            lastActivated = System.currentTimeMillis()
        }
    }
}

fun View.onLongClick(action: () -> Unit) {
    setOnLongClickListener { action(); true }
}


fun View.setBackgroundColorResource(color: ColorResource) {
    setBackgroundResource(color)
}
