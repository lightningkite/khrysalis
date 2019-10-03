package com.lightningkite.kwift.views.shared

import com.lightningkite.kwift.actuals.escaping
import com.lightningkite.kwift.actuals.formatList
import com.lightningkite.kwift.actuals.swiftExactly
import com.lightningkite.kwift.views.actual.StringResource
import com.lightningkite.kwift.views.actual.ViewDependency
import com.lightningkite.kwift.views.actual.getString


interface ViewString {
    fun get(dependency: ViewDependency): String
}

class ViewStringRaw(val string: String) : ViewString {
    override fun get(dependency: ViewDependency): String = string
}

class ViewStringResource(val resource: StringResource) : ViewString {
    override fun get(dependency: ViewDependency): String = dependency.getString(resource)
}

class ViewStringTemplate(val template: ViewString, val arguments: List<Any>) : ViewString {
    override fun get(dependency: ViewDependency): String {
        val templateResolved = template.get(dependency)
        val fixedArguments = arguments.map { it -> (it as? ViewString)?.get(dependency) ?: it }
        return templateResolved.formatList(fixedArguments)
    }
}

class ViewStringComplex(val getter: @escaping() (ViewDependency) -> String) : ViewString {
    override fun get(dependency: ViewDependency): String = getter(dependency)
}

fun ViewStringList(others: List<ViewString>): ViewStringComplex = ViewStringComplex { dependency ->
    others.joinToString("\n") { it -> it.get(dependency) }
}

fun List<@swiftExactly() ViewString>.joinToViewString(separator: String = "\n"): ViewStringComplex =
    ViewStringComplex { dependency ->
        this.joinToString(separator) { it -> it.get(dependency) }
    }
