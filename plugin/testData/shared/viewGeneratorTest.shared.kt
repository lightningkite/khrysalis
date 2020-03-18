package com.lightningkite.khrysalis.views.shared

import android.view.View
import com.lightningkite.khrysalis.views.actual.ViewDependency

abstract class ViewGenerator {
    abstract val title: String

    abstract fun generate(dependency: ViewDependency): View
}
