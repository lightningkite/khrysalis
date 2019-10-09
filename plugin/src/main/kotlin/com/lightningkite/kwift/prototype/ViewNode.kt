package com.lightningkite.kwift.prototype

import com.lightningkite.kwift.utils.XmlNode
import com.lightningkite.kwift.utils.camelCase

class ViewNode(
    val name: String
) {
    val operations: HashSet<ViewStackOp> = HashSet()
    val requires: HashSet<ViewVar> = HashSet()
    val provides: HashSet<ViewVar> = HashSet()
    val instantiates: Set<String> get() = operations.mapNotNull { it.viewName }.toSet()

    companion object {
        //stackOperation, stackId, stackTarget
        val stackTargetRegex = Regex("""tools:goTo *= *"@layout/([a-z_A-Z0-9]+)" *""")
        val stackTarget2Regex = Regex("""tools:swap *= *"@layout/([a-z_A-Z0-9]+)" *""")
        val stackPopRegex = Regex("""tools:pop *= """)
        val stackIdRegex = Regex("""tools:onStack *= *"([a-z_A-Z0-9]+)" *""")

        //bindStack, stackDefault
        val stackDefaultRegex = Regex("""tools:stackDefault *= *"@layout/([a-z_A-Z0-9]+)" *""")
        val bindStackRegex = Regex("""tools:stackId *= *"([a-z_A-Z0-9]+)" *""")

        val requiresRegex = Regex("""tools:requires *= *"([a-z_A-Z0-9]+): *([a-z_A-Z0-9]+)" *""")
        val providesRegex = Regex("""tools:provides *= *"([a-z_A-Z0-9]+): *([a-z_A-Z0-9]+)" *""")

        const val attributePush = "tools:goTo"
        const val attributeSwap = "tools:swap"
        const val attributePop = "tools:pop"
        const val attributeOnStack = "tools:onStack"
        const val attributeStackDefault = "tools:stackDefault"
        const val attributeStackId = "tools:stackId"
        const val attributeRequires = "tools:requires"
        const val attributeProvides = "tools:provides"

        const val defaultStack = "owningStack"
    }

    fun totalRequires(map: Map<String, ViewNode>, seen: Set<String> = setOf()): Set<ViewVar> {
        if (name in seen) return setOf()
        return requires + (instantiates.flatMap {
            map[it]?.totalRequires(map, seen + name) ?: setOf()
        }.toSet() - provides)
    }

    fun estimateDepth(map: Map<String, ViewNode>, seen: Set<String> = setOf()): Int {
        if (name in seen) return 0
        return requires.size + instantiates.sumBy {
            map[it]?.estimateDepth(map, seen + name) ?: 0
        } - provides.size
    }

    fun belongsToStacks(map: Map<String, ViewNode>): Set<String?> {
        return map.values.asSequence()
            .flatMap { it.operations.asSequence() }
            .filter { it.viewName == name }
            .map { it.stack }
            .toSet()
    }

    fun gather(node: XmlNode) {
        node.attributes[attributePush]?.let {
            val onStack = node.attributes[attributeOnStack]
            operations.add(
                ViewStackOp.Push(
                    stack = onStack,
                    viewName = it.removePrefix("@layout/").camelCase().capitalize()
                )
            )
            onStack?.let { requires.add(
                ViewVar(
                    onStack,
                    "ObservableStack[ViewGenerator]"
                )
            ) }
        }
        node.attributes[attributeSwap]?.let {
            val onStack = node.attributes[attributeOnStack]
            operations.add(
                ViewStackOp.Swap(
                    stack = onStack,
                    viewName = it.removePrefix("@layout/").camelCase().capitalize()
                )
            )
            onStack?.let { requires.add(
                ViewVar(
                    onStack,
                    "ObservableStack[ViewGenerator]"
                )
            ) }
        }
        node.attributes[attributePop]?.let {
            val onStack = node.attributes[attributeOnStack]
            operations.add(ViewStackOp.Pop(stack = onStack))
        }
        node.attributes[attributeStackId]?.let { stackId ->
            provides.add(ViewVar(stackId, "ObservableStack[ViewGenerator]"))
            node.attributes[attributeStackDefault]?.let {
                operations.add(
                    ViewStackOp.Embed(
                        stack = stackId,
                        viewName = it.removePrefix("@layout/").camelCase().capitalize()
                    )
                )
            }
        }
        node.attributes[attributeRequires]?.let {
            requires.add(ViewVar(it, "ObservableStack[ViewGenerator]"))
        }
        node.children.forEach { gather(it) }
    }
}
