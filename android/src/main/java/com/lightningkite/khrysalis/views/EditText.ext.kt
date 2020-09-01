package com.lightningkite.khrysalis.views

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import java.util.*

fun EditText.setOnDoneClick(action: () -> Unit) {
    this.setOnEditorActionListener { v, actionId, event ->
        action()
        true
    }
}

private val View_focusAtStartup = WeakHashMap<View, Boolean>()
var View.focusAtStartup: Boolean
    get() = View_focusAtStartup[this] ?: true
    set(value) {
        View_focusAtStartup[this] = value
    }