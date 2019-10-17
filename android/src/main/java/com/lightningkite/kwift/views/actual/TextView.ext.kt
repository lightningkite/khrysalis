package com.lightningkite.kwift.views.actual

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.*

var TextView.textResource: Int
    get() = 0
    set(value) { setText(value) }
var TextView.textString: String
    get() = text.toString()
    set(value) { setText(value) }


var ToggleButton.textResource: Int
    get() = 0
    set(value) {
        setText(value)
        textOn = resources.getString(value)
        textOff = resources.getString(value)
    }
var ToggleButton.textString: String
    get() = text.toString()
    set(value) {
        setText(value)
        textOn = value
        textOff = value
    }
