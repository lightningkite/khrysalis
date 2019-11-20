package com.lightningkite.kwift.views.android

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

class SelectableText : TextView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    init {
        this.setTextIsSelectable(true)
    }

}