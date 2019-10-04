package com.lightningkite.kwift.views.shared

import android.view.View
import com.lightningkite.kwift.views.actual.ViewDependency

abstract class ViewGenerator {
    abstract val title: String

    abstract fun generate(dependency: ViewDependency): View
}
