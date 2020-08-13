package com.lightningkite.khrysalis.views

import android.view.inputmethod.EditorInfo
import android.widget.EditText

fun EditText.setOnDoneClick(action: ()->Unit) {
    this.setOnEditorActionListener { v, actionId, event ->
        action()
        true
    }
}