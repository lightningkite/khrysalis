package com.lightningkite.khrysalis.views.android

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView

class SelectableText : AppCompatTextView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    init {
        this.setTextIsSelectable(true)
    }

}
