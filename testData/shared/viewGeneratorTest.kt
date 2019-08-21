package com.lightningkite.kwift.views.shared

import android.view.View
import com.lightningkite.kwift.views.actual.ViewDependency

interface ViewGenerator {
    val title: String

    fun generate(dependency: ViewDependency): View
}


class MainViewGenerator : ViewGenerator {
    override val title: String get() = "Main"

    val stack: ObservableStack<ViewGenerator> = ObservableStack<ViewGenerator>()

    init {
        stack.push(ExampleContentViewData(stack))
    }

    override fun generate(dependency: ViewDependency): View {
        val xml = MainXml()
        val view = xml.setup(dependency)

        xml.boundViewMainContent.bindStack(dependency, stack)
        xml.boundViewTitle.bindText(stack){ it -> it.lastOrNull()?.title ?: "" }
        xml.boundViewMainBack.bindVisible(stack.transformed { it -> it.size > 1 })
        xml.boundViewMainBack.onClick(captureWeak(this) { self -> self.stack.pop(); Unit })

        return view
    }
}
