package com.lightningkite.kwift.prototype

import java.io.BufferedWriter
import java.io.File


internal fun groupedGraph(
    outputFolder: File,
    nodes: HashMap<String, ViewNode>
) {
    outputFolder.resolve("flow-grouped.mermaid").bufferedWriter().use { out ->
        val groupedNodes = nodes.entries
            .groupBy {
                val belongsTo = it.value.belongsToStacks(nodes)
                if (belongsTo.size == 1) belongsTo.first()
                else ""
            }
            .mapValues { it.value.sortedBy { it.value.estimateDepth(nodes) } }
            .entries
            .sortedBy { it.value.map { it.value.estimateDepth(nodes) }.average() }
        val nodeId: Map<String, String> = groupedNodes
            .flatMap { it.value }
            .withIndex()
            .associate {
                it.value.key to getShortIdentifier(it.index)
            }

        out.appendln("graph LR;")
        groupedNodes.forEach {
            if (!it.key.isNullOrBlank()) {
                out.appendln("subgraph ${it.key};")
            }
            it.value.forEach {
                out.appendln("${nodeId[it.key]}[${it.key}];")
            }
            if (!it.key.isNullOrBlank()) {
                out.appendln("end;")
            }
        }
        groupedNodes.flatMap { it.value }.forEach { entry ->
            entry.value.operations.forEach { operation ->
                mermaidEmitLink(out, nodes, nodeId, entry, operation)
            }
        }
    }
}

internal fun sortedGraph(
    outputFolder: File,
    nodes: HashMap<String, ViewNode>
) {
    outputFolder.resolve("flow-sorted.mermaid").bufferedWriter().use { out ->
        val groupedNodes = nodes.entries
            .sortedBy { it.value.estimateDepth(nodes) }
        val nodeId: Map<String, String> = groupedNodes
            .withIndex()
            .associate {
                it.value.key to getShortIdentifier(it.index)
            }

        out.appendln("graph LR;")
        groupedNodes.forEach {
            out.appendln("${nodeId[it.key]}[${it.key}];")
        }
        groupedNodes.forEach { entry ->
            entry.value.operations.forEach { operation ->
                mermaidEmitLink(out, nodes, nodeId, entry, operation)
            }
        }
    }
}

internal fun getShortIdentifier(index: Int): String {
    return when (index) {
        in Int.MIN_VALUE..-1 -> "UNK"
        in 0..25 -> ('A' + index).toString()
        in 26..675 -> ('A' + index / 26).toString() + ('A' + index % 26).toString()
        else -> ('A' + index / 676).toString() + ('A' + index / 26 % 26).toString() + ('A' + index % 26).toString()
    }
}

internal fun mermaidEmitLink(
    out: BufferedWriter,
    nodes: HashMap<String, ViewNode>,
    nodeId: Map<String, String>,
    entry: MutableMap.MutableEntry<String, ViewNode>,
    operation: ViewStackOp
) {
    val target = operation.viewName ?: return
    val targetId = nodeId[target]
    val passed = nodes[target]
        ?.totalRequires(nodes)
        ?.joinToString { it.name }
        ?.replace('[', '<')
        ?.replace(']', '>')
        ?.takeUnless { it.isEmpty() }
        ?: ""
    val arrowChars = when (operation) {
        is ViewStackOp.Pop -> "--"
        is ViewStackOp.Push -> "--"
        is ViewStackOp.Swap -> "-."
        is ViewStackOp.Embed -> "=="
    }
    val arrowCharsSingle = when (operation) {
        is ViewStackOp.Pop -> "--"
        is ViewStackOp.Push -> "--"
        is ViewStackOp.Swap -> "-.-"
        is ViewStackOp.Embed -> "=="
    }
    if (passed.isEmpty())
        out.appendln("${nodeId[entry.key]}${arrowCharsSingle}>${targetId};")
    else
        out.appendln("${nodeId[entry.key]}${arrowChars} $passed ${arrowChars.reversed()}>${targetId};")
}
