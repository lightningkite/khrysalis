package com.lightningkite.kwift.views.actual

import android.widget.ToggleButton


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