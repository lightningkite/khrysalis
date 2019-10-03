package com.lightningkite.kwift.views.actual

import android.view.View

fun View.onClick(action: () -> Unit) {
    setOnClickListener { action() }
}
