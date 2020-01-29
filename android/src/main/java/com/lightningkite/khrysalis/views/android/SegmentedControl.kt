package com.lightningkite.khrysalis.views.android

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.tabs.TabLayout

class SegmentedControl : TabLayout {
    constructor(context: Context) : super(context) { sharedInit() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { sharedInit() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { sharedInit() }

    fun sharedInit(){

    }
}
