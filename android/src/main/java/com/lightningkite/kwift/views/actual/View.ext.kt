package com.lightningkite.kwift.views.actual

import android.view.View

fun View.onClick(action: () -> Unit) {
    setOnClickListener { action() }
}

fun View(dependency: ViewDependency): View = View(dependency.context)
