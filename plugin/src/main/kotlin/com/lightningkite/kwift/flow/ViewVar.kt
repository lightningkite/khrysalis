package com.lightningkite.kwift.flow

class ViewVar(val name: String, val type: String, val default: String?) {
    val kotlinType: String get() = type.replace('[', '<').replace(']', '>')
    override fun toString(): String = "$name: $kotlinType" + (if(default != null) " = ${default.replace('[', '<').replace(']', '>')}" else "")
    fun construct(viewNode: ViewNode, viewNodeMap: Map<String, ViewNode>): String {
        return default?.replace('[', '<')?.replace(']', '>') ?: constructors[type]?.invoke(viewNode, viewNodeMap) ?: "$kotlinType()"
    }

    companion object {
        val constructors = HashMap<String, (viewNode: ViewNode, viewNodeMap: Map<String, ViewNode>)->String>()
        init {
            constructors["ObservableStack[ViewGenerator]"] = { node, nodeMap -> "ObservableStack()" }
            constructors["Int"] = { node, nodeMap -> "0" }
            constructors["Long"] = { node, nodeMap -> "0L" }
            constructors["String"] = { node, nodeMap -> "\"\"" }
            constructors["Float"] = { node, nodeMap -> "0f" }
            constructors["Double"] = { node, nodeMap -> "0.0" }
            constructors["Boolean"] = { node, nodeMap -> "false" }
        }
    }

    override fun equals(other: Any?): Boolean = other is ViewVar && other.name == name
    override fun hashCode(): Int = name.hashCode() + 1
}
