package com.lightningkite.kwift.flow

import com.lightningkite.kwift.layout.Styles
import com.lightningkite.kwift.utils.XmlNode
import com.lightningkite.kwift.utils.camelCase
import java.io.File

class ViewNode(
    val name: String
) {
    val operations: HashSet<ViewStackOp> = HashSet()
    val requires: HashSet<ViewVar> = HashSet()
    val provides: HashSet<ViewVar> = HashSet()
    val instantiates: Set<String> get() = operations.mapNotNull { it.viewName }.toSet()

    companion object {
        val stack = ViewVar(
            "stack",
            "ObservableStack[ViewGenerator]",
            null
        )

        const val attributePush = "tools:goTo"
        const val attributeSwap = "tools:swap"
        const val attributePop = "tools:pop"
        const val attributeDismiss = "tools:dismiss"
        const val attributeReset = "tools:reset"
        const val attributeOnStack = "tools:onStack"
        const val attributeStackDefault = "tools:stackDefault"
        const val attributeStackId = "tools:stackId"
        const val attributeRequires = "tools:requires"
        const val attributeProvides = "tools:provides"
    }

    fun totalRequires(map: Map<String, ViewNode>, seen: Set<String> = setOf()): Set<ViewVar> {
        if (name in seen) return setOf()
        return (requires + (instantiates.flatMap {
            map[it]?.totalRequires(map, seen + name)?.filter { it.default == null }?.toSet() ?: setOf()
        }.filter { it.name != "stack" }.toSet()) - provides)
    }

    fun estimateDepth(map: Map<String, ViewNode>, seen: Set<String> = setOf()): Int {
        if (name in seen) return 0
        return requires.size + instantiates.sumBy {
            map[it]?.estimateDepth(map, seen + name) ?: 0
        } - provides.size
    }

    //    fun belongsToStacks(map: Map<String, ViewNode>): Set<String> {
//        return map.values.asSequence()
//            .flatMap { node ->
//                node.operations.asSequence()
//                    .filter { it.viewName == name }
//                    .flatMap { it.stack?.let { sequenceOf(it) } ?: node.belongsToStacks(map).asSequence() }
//            }
//            .toSet()
//    }
    fun belongsToStacks(map: Map<String, ViewNode>): Set<String> {
        return map.values.asSequence()
            .flatMap { it.operations.asSequence() }
            .filter { it.viewName == name }
            .mapNotNull { it.stack }
            .toSet()
    }

    fun gather(node: XmlNode, xml: File, styles: Styles) {
        node.attributes[attributePush]?.let {
            val onStack = node.attributes[attributeOnStack] ?: "stack"
            operations.add(
                ViewStackOp.Push(
                    stack = onStack,
                    viewName = it.removePrefix("@layout/").camelCase().capitalize()
                )
            )
            requires.add(
                ViewVar(
                    name = onStack,
                    type = "ObservableStack[ViewGenerator]",
                    default = null
                )
            )
        }
        node.attributes[attributeSwap]?.let {
            val onStack = node.attributes[attributeOnStack] ?: "stack"
            operations.add(
                ViewStackOp.Swap(
                    stack = onStack,
                    viewName = it.removePrefix("@layout/").camelCase().capitalize()
                )
            )
            requires.add(
                ViewVar(
                    name = onStack,
                    type = "ObservableStack[ViewGenerator]",
                    default = null
                )
            )
        }
        node.attributes[attributeReset]?.let {
            val onStack = node.attributes[attributeOnStack] ?: "stack"
            operations.add(
                ViewStackOp.Reset(
                    stack = onStack,
                    viewName = it.removePrefix("@layout/").camelCase().capitalize()
                )
            )
            requires.add(
                ViewVar(
                    name = onStack,
                    type = "ObservableStack[ViewGenerator]",
                    default = null
                )
            )
        }
        node.attributes[attributePop]?.let {
            (node.attributes[attributeOnStack]?.split(';') ?: listOf("stack")).forEach {
                operations.add(ViewStackOp.Pop(stack = it))
                requires.add(
                    ViewVar(
                        name = it,
                        type = "ObservableStack[ViewGenerator]",
                        default = null
                    )
                )
            }
        }
        node.attributes[attributeDismiss]?.let {
            (node.attributes[attributeOnStack]?.split(';') ?: listOf("stack")).forEach {
                operations.add(ViewStackOp.Dismiss(stack = it))
                requires.add(
                    ViewVar(
                        name = it,
                        type = "ObservableStack[ViewGenerator]",
                        default = null
                    )
                )
            }
        }
        node.attributes[attributeStackId]?.let { stackId ->
            provides.add(ViewVar(stackId, "ObservableStack[ViewGenerator]", "ObservableStack()"))
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
            it.split(';').forEach {
                requires.add(ViewVar(
                    name = it.substringBefore(':').trim(),
                    type = it.substringAfter(':').substringBefore('=').trim(),
                    default = it.substringAfter('=', "").takeUnless { it.isEmpty() }?.trim()
                ))
            }
        }
        node.attributes[attributeProvides]?.let {
            it.split(';').forEach {
                provides.add(ViewVar(
                    name = it.substringBefore(':').trim(),
                    type = it.substringAfter(':').substringBefore('=').trim(),
                    default = it.substringAfter('=', "").takeUnless { it.isEmpty() }?.trim()
                ))
            }
        }
        if (node.name == "include") {
            node.attributes["layout"]?.let {
                val file = xml.parentFile.resolve(it.removePrefix("@layout/").plus(".xml"))
                gather(XmlNode.read(file, styles), xml, styles)
            }
        }
        node.attributes["tools:listitem"]?.let {
            val file = xml.parentFile.resolve(it.removePrefix("@layout/").plus(".xml"))
            gather(XmlNode.read(file, styles), xml, styles)
        }
        node.children.forEach { gather(it, xml, styles) }
    }
}
