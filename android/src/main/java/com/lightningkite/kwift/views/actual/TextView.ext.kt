package com.lightningkite.kwift.views.actual

import android.widget.TextView

var TextView.textResource: Int
    get() = 0
    set(value) {
        setText(value)
    }

var TextView.textString: String
    get() = text.toString()
    set(value) {
        setText(value)
    }

fun TextView.setColor(color: ColorResource) {
    setTextColor(resources.getColor(color))
}



