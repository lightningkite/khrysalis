package com.lightningkite.kwift.views.shared

import android.view.View
import com.lightningkite.kwift.views.actual.EmptyView
import com.lightningkite.kwift.views.actual.ViewDependency

abstract class ViewGenerator {
    abstract val title: String

    abstract fun generate(dependency: ViewDependency): View

    class Default(): ViewGenerator() {
        override val title: String
            get() = "Empty"

        override fun generate(dependency: ViewDependency): View = EmptyView(dependency)

    }
}
