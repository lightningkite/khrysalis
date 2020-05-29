package com.lightningkite.khrysalis.flow

import com.lightningkite.khrysalis.ios.layout.Styles
import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.camelCase
import java.io.File
import kotlin.math.max

class ViewNode(
    val name: String
) {
    override fun equals(other: Any?): Boolean {
        return other is ViewNode && this.name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return name
    }

    val operations: HashSet<ViewStackOp> = HashSet()
    val requires: HashSet<ViewVar> = HashSet()
    val provides: HashSet<ViewVar> = HashSet()
    val instantiates: Set<String> get() = operations.mapNotNull { it.viewName }.toSet()

    var depth: Int = -1

    data class Resolved(
        val node: ViewNode,
        val comesFrom: Set<String>,
        val totalRequires: Set<String>,
        val belongsToStacks: Set<String>
    )

    fun resolve(map: Map<String, ViewNode>): Resolved {
        return Resolved(
            node = this,
            comesFrom = this.createdBy(map),
            totalRequires = this.totalRequires(map).map { it.name }.toSet(),
            belongsToStacks = this.belongsToStacks(map)
        )
    }

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
        const val attributePopTo = "tools:popTo"
        const val attributeOnStack = "tools:onStack"
        const val attributeStackDefault = "tools:stackDefault"
        const val attributeStackId = "tools:stackId"
        const val attributeRequires = "tools:requires"
        const val attributeProvides = "tools:provides"

        //Breadth-first search
        fun estimateDepth(map: Map<String, ViewNode>) {
            map.values.forEach { it.depth = -1 }
            val root = root(map) ?: return
            root.depth = 0
            var highestSeen = 0
            val seen = mutableListOf<String>()
            val stack = mutableListOf(root)
            while (stack.isNotEmpty()) {
                val next = stack.removeAt(0)
                highestSeen = max(highestSeen, next.depth)
                for (item in next.instantiates) {
                    if (item in seen) continue
                    seen.add(item)
                    map[item]?.let {
                        it.depth = next.depth + 1
                        stack.add(it)
                    }
                }
            }

            map.values.forEach {
                if (it.depth == -1) {
                    it.depth = highestSeen
                }
            }
        }

        fun root(map: Map<String, ViewNode>) = map["Root"] ?: map["Main"] ?: map["Landing"] ?: map.values.firstOrNull()

        fun assertNoLeaks(map: Map<String, ViewNode>) {
            val root = root(map) ?: return
            if (root.totalRequires(map).isEmpty()) return
            val leakMessages = ArrayList<String>()
            for (leakedVar in root.totalRequires(map)) {
                if (leakedVar.default != null) continue
                val requiredBy = map.values.filter { leakedVar in it.requires && leakedVar !in it.provides }
                val climbing = requiredBy.map { listOf(it) }.toMutableList()
                val seen = mutableSetOf<String>()
                while (climbing.isNotEmpty()) {
                    val next = climbing.removeAt(0)
                    for (it in next.last().createdBy(map)) {
                        if (it in seen) continue
                        seen.add(it)
                        val node = map[it] ?: continue
                        if (leakedVar in node.provides) continue
                        if (node == root) {
                            val message = "Leak path for ${leakedVar}: ${next.joinToString(" <- ") { it.name }}"
                            leakMessages += message
                            println(message)
                            break
                        }
                        climbing.add(next + node)
                    }
                }
            }

            throw Exception("Leak detected! ${leakMessages.joinToString("\n")}")
        }
    }

    fun totalRequires(map: Map<String, ViewNode>, seen: Set<String> = setOf()): Set<ViewVar> {
        if (name in seen) return setOf()
        return ((instantiates.flatMap {
            map[it]?.totalRequires(map, seen + name)?.filter { it.default == null }?.toSet() ?: setOf()
        }.filter { it.name != "stack" }.toSet()) + requires.filter { it.default == null }.toSet() - provides)
    }

    fun belongsToStacks(map: Map<String, ViewNode>): Set<String> {
        return map.values.asSequence()
            .flatMap { it.operations.asSequence() }
            .filter { it.viewName == name }
            .mapNotNull { it.stack }
            .toSet()
    }

    fun createdBy(map: Map<String, ViewNode>): Set<String> {
        return map.values.asSequence()
            .filter { it.operations.asSequence().any { it.viewName == this.name } }
            .mapNotNull { it.name }
            .toSet()
    }

    fun gather(node: XmlNode, xml: File, styles: Styles, parentPath: String? = "xml") {
        val path = parentPath?.let { parentPath ->
            node.allAttributes["android:id"]?.removePrefix("@+id/")?.camelCase()?.let {
                parentPath + "." + it
            }
        }
        node.allAttributes[attributePush]?.let {
            val onStack = node.allAttributes[attributeOnStack] ?: "stack"
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
        node.allAttributes[attributeSwap]?.let {
            val onStack = node.allAttributes[attributeOnStack] ?: "stack"
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
        node.allAttributes[attributeReset]?.let {
            val onStack = node.allAttributes[attributeOnStack] ?: "stack"
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
        node.allAttributes[attributePopTo]?.let {
            val onStack = node.allAttributes[attributeOnStack] ?: "stack"
            operations.add(
                ViewStackOp.PopTo(
                    stack = onStack,
                    viewType = it.removePrefix("@layout/").camelCase().capitalize()
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
        node.allAttributes[attributePop]?.let {
            (node.allAttributes[attributeOnStack]?.split(';') ?: listOf("stack")).forEach {
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
        node.allAttributes[attributeDismiss]?.let {
            (node.allAttributes[attributeOnStack]?.split(';') ?: listOf("stack")).forEach {
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
        node.allAttributes[attributeStackId]?.let { stackId ->
            provides.add(ViewVar(stackId, "ObservableStack[ViewGenerator]", "ObservableStack()"))
            node.allAttributes[attributeStackDefault]?.let {
                operations.add(
                    ViewStackOp.Embed(
                        stack = stackId,
                        viewName = it.removePrefix("@layout/").camelCase().capitalize()
                    )
                )
            }
        }
        node.allAttributes[attributeRequires]?.let {
            it.split(';').filter { it.isNotBlank() }.forEach {
                val newVar = ViewVar(
                    name = it.substringBefore(':').trim(),
                    type = it.substringAfter(':').substringBefore('=').trim(),
                    default = it.substringAfter('=', "").takeUnless { it.isEmpty() }?.trim(),
                    onPath = path
                )
                println("newVar: $newVar")
                requires.add(newVar)
            }
        }
        node.allAttributes[attributeProvides]?.let {
            it.split(';').filter { it.isNotBlank() }.forEach {
                provides.add(
                    ViewVar(
                        name = it.substringBefore(':').trim(),
                        type = it.substringAfter(':').substringBefore('=').trim(),
                        default = it.substringAfter('=', "").takeUnless { it.isEmpty() }?.trim(),
                        onPath = path
                    )
                )
            }
        }
        if (node.name == "include") {
            node.allAttributes["layout"]?.let {
                val file = xml.parentFile.resolve(it.removePrefix("@layout/").plus(".xml"))
                gather(XmlNode.read(file, styles), xml, styles, path)
            }
        }
        node.allAttributes["tools:listitem"]?.let {
            val file = xml.parentFile.resolve(it.removePrefix("@layout/").plus(".xml"))
            gather(XmlNode.read(file, styles), xml, styles, parentPath)
        }
        node.children.forEach { gather(it, xml, styles, parentPath) }
    }
}
