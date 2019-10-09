package com.lightningkite.kwift.flow

data class ViewVar(val name: String, val type: String) {
    val kotlinType: String get() = type.replace('[', '<').replace(']', '>')
    override fun toString(): String = "$name: $kotlinType"

    companion object {
        val constructors = HashMap<String, (viewNode: ViewNode, viewNodeMap: Map<String, ViewNode>)->String>()
        init {
            constructors["ObservableStack<ViewGenerator>"] = { node, nodeMap -> "ObservableStack()" }
        }
        fun construct(viewNode: ViewNode, viewNodeMap: Map<String, ViewNode>, type: String): String {
            return constructors[type]?.invoke(viewNode, viewNodeMap) ?: "$type()"
        }
    }
}
