package com.lightningkite.khrysalis.views.shared

import com.lightningkite.khrysalis.actuals.escaping
import com.lightningkite.khrysalis.actuals.formatList
import com.lightningkite.khrysalis.views.actual.StringResource
import com.lightningkite.khrysalis.views.actual.ViewDependency
import com.lightningkite.khrysalis.views.actual.getString

data class ManyThings(val x: Int, val y: Int, val z: Int){
    fun stringy() = "$x$y$z"
}

interface ViewString {
    fun get(dependency: ViewDependency): String
}

class ViewStringRaw(val string: String) : ViewString {
    override fun get(dependency: ViewDependency): String = string
}

class ViewStringResource(val resource: StringResource) : ViewString {
    override fun get(dependency: ViewDependency): String = dependency.getString(resource)
}

class ViewStringTemplate(val template: ViewString, val arguments: List<Any?>) : ViewString {
    override fun get(dependency: ViewDependency): String {
        val template = template.get(dependency)
//        val arguments = arguments.map { it -> return@map if(it is ViewString) it.get(dependency) else it }
        val arguments = arguments.map { it -> (it as? ViewString)?.get(dependency) ?: it }
        return template.formatList(arguments)
    }
}

class ViewStringComplex(val getter: @escaping() (ViewDependency) -> String) : ViewString {
    override fun get(dependency: ViewDependency): String = getter(dependency)
}

fun ViewStringList(others: List<ViewString>) = ViewStringComplex { dependency ->
    others.joinToString("\n") { it.get(dependency) }
}

fun List<ViewString>.joinToString(separator: String = "\n") = ViewStringComplex { dependency ->
    joinToString(separator) { it.get(dependency) }
}
