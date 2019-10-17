package com.lightningkite.kwift.views.actual

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

var View.backgroundDrawable: Drawable?
    get() = this.background
    set(value) {
        this.background = value
    }
