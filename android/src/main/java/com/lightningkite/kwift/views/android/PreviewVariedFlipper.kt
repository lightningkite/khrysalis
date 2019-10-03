package com.lightningkite.kwift.views.android

import android.content.Context
import android.util.AttributeSet
import android.widget.ViewFlipper

class PreviewVariedFlipper : ViewFlipper {

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    companion object {
        var currentNumber = Math.random().times(100).toInt()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        displayedChild = (currentNumber++) % childCount
    }
}
