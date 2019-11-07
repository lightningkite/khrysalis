package com.lightningkite.kwift.views.actual

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

fun View.onClick(action: () -> Unit) {
    setOnClickListener { action() }
}

fun View.onLongClick(action: () -> Unit) {
    setOnLongClickListener { action(); true }
}
