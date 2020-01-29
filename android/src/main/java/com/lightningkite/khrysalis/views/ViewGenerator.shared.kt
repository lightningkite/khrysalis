package com.lightningkite.khrysalis.views

import android.view.View
import com.lightningkite.khrysalis.views.EmptyView
import com.lightningkite.khrysalis.views.ViewDependency

abstract class ViewGenerator {
    abstract val title: String

    abstract fun generate(dependency: ViewDependency): View

    class Default(): ViewGenerator() {
        override val title: String
            get() = "Empty"

        override fun generate(dependency: ViewDependency): View = EmptyView(dependency)

    }
}
