package com.lightningkite.kwift.views.actual

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

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
