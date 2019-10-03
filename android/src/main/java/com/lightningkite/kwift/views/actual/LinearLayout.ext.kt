package com.lightningkite.kwift.views.actual

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout

fun LinearLayout.append(view: View, fill: Boolean = true) {
    this.addView(
        view,
        ViewGroup.LayoutParams(
            if (this.orientation == LinearLayout.VERTICAL && fill) MATCH_PARENT else WRAP_CONTENT,
            if (this.orientation == LinearLayout.HORIZONTAL && fill) MATCH_PARENT else WRAP_CONTENT
        )
    )
}

fun LinearLayout.clear() {
    this.removeAllViews()
}
