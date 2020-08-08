package com.lightningkite.khrysalis.views

import android.view.View

abstract class ViewGenerator {
    @Deprecated("Use titleString instead for localizations", ReplaceWith("titleString"))
    open val title: String get() = ""
    @Suppress("DEPRECATION")
    open val titleString: ViewString get() = ViewStringRaw(title)

    abstract fun generate(dependency: ViewDependency): View

    class Default(): ViewGenerator() {
        override fun generate(dependency: ViewDependency): View = newEmptyView(dependency)
    }
}
