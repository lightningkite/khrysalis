package com.lightningkite.kwift.views.actual

import android.view.View
import android.widget.EditText
import android.widget.TextView

fun View.onClick(action: () -> Unit) {
    setOnClickListener { action() }
}

fun EmptyView(dependency: ViewDependency): View = View(dependency.context)

var TextView.textResource: Int
    get() = 0
    set(value) { setText(value) }
var TextView.textString: String
    get() = text.toString()
    set(value) { setText(value) }
