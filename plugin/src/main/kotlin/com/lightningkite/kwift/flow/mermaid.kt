package com.lightningkite.kwift.flow

import java.io.BufferedWriter
import java.io.File


internal fun groupedGraph(
    outputFolder: File,
    nodes: Map<String, ViewNode>
) {
    println("Making groupedGraph")
    outputFolder.resolve("flow-grouped.mermaid").bufferedWriter().use { out ->
        val groupedNodes = groupByBelonging(nodes)
        val nodeId: Map<String, String> = groupedNodes
            .flatMap { it.value }
            .withIndex()
            .associate {
                it.value.key to getShortIdentifier(it.index)
            }
        groupedNodes
            .flatMap { it.value.map { it.value } }
            .filter { nodeId[it.name] == null }
            .forEach {
                println("Mismatch: $it")
            }

        out.appendln("graph LR;")
        groupedNodes.forEach {
            if (!it.key.isBlank() && it.key != "stack") {
                out.appendln("subgraph ${it.key};")
            }
            it.value.forEach {
                out.appendln("${nodeId[it.key]}[${it.key}];")
            }
            if (!it.key.isBlank() && it.key != "stack") {
                out.appendln("end;")
            }
        }
        groupedNodes.flatMap { it.value }.forEach { entry ->
            entry.value.operations.forEach { operation ->
                mermaidEmitLink(out, nodes, nodeId, entry.value, operation)
            }
        }
    }
}

private fun groupByBelonging(nodes: Map<String, ViewNode>): Map<String, List<Map.Entry<String, ViewNode>>> {
    return nodes
        .entries
        .groupBy {
            val belongsTo = it.value.belongsToStacks(nodes)
            if (belongsTo.size == 1) belongsTo.first()
            else ""
        }
        .mapValues { it.value.sortedBy { it.value.estimateDepth(nodes) } }
}

private fun multigroupByBelonging(nodes: Map<String, ViewNode>): Map<String, List<Map.Entry<String, ViewNode>>> {
    val stacks = nodes.values.asSequence().flatMap { it.operations.asSequence().mapNotNull { it.stack } }.toSet()
    return stacks.associate { stack ->
        stack to nodes.filter { stack in it.value.belongsToStacks(nodes) }
    }.mapValues { it.value.entries.sortedBy { it.value.estimateDepth(nodes) } }
}

internal fun sortedGraph(
    outputFolder: File,
    nodes: Map<String, ViewNode>
) {
    println("Making sortedGraph")
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
                mermaidEmitLink(out, nodes, nodeId, entry.value, operation)
            }
        }
    }
}

internal fun partialGraphs(
    outputFolder: File,
    nodes: Map<String, ViewNode>
) {
    println("Making partialGraphs")
    val groupedNodes = multigroupByBelonging(nodes)
        .mapValues { it.value.sortedBy { it.value.estimateDepth(nodes) } }
    for ((group, values) in groupedNodes) {
        println("Making partialGraph for $group")
        if (group.isEmpty() || group == "stack") continue
        outputFolder.resolve("flow-partial-$group.mermaid").bufferedWriter().use { out ->
            val internalNodes = values.map { it.value }.toSet()
            val internalNodeNames = internalNodes.map { it.name }.toSet()
            val beforeNodes = (nodes.values.toSet() - internalNodes)
                .filter {
                    it.operations.any { it.viewName in internalNodeNames && it.stack == group }
                }.toSet()
            val afterNodes = internalNodes
                .flatMap {
                    it.operations.mapNotNull {
                        if (it.stack == group)
                            it.viewName?.let { nodes[it] }
                        else null
                    }
                }.toSet() - internalNodes
            val subsetNodes = (beforeNodes + internalNodes + afterNodes)
            val subsetNodeNames = subsetNodes.map { it.name }.toSet()
            val nodeId: Map<String, String> = subsetNodes
                .withIndex()
                .associate {
                    it.value.name to getShortIdentifier(it.index)
                }
            subsetNodes
                .filter { nodeId[it.name] == null }
                .forEach {
                    println("Mismatch: $it")
                }

            out.appendln("graph LR;")

            beforeNodes.forEach {
                out.appendln("${nodeId[it.name]}[${it.name}];")
            }

            out.appendln("subgraph $group;")
            internalNodes.forEach {
                out.appendln("${nodeId[it.name]}[${it.name}];")
            }
            out.appendln("end;")

            afterNodes.forEach {
                out.appendln("${nodeId[it.name]}[${it.name}];")
            }

            (beforeNodes).forEach { node ->
                node.operations.forEach { operation ->
                    if (operation.viewName in subsetNodeNames && operation.stack == group) {
                        mermaidEmitLink(out, nodes, nodeId, node, operation)
                    }
                }
            }
            internalNodes.forEach { node ->
                node.operations.forEach { operation ->
                    mermaidEmitLink(out, nodes, nodeId, node, operation)
                }
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
    nodes: Map<String, ViewNode>,
    nodeId: Map<String, String>,
    node: ViewNode,
    operation: ViewStackOp
) {
    val target = operation.viewName ?: return
    val targetId = nodeId[target] ?: run {
        println("No nodeId for $target")
        return
    }
    val passed = nodes[target]
        ?.totalRequires(nodes)
        ?.joinToString { it.name }
        ?.replace('[', '<')
        ?.replace(']', '>')
        ?.takeUnless { it.isEmpty() }
        ?: ""
    val arrowChars = when (operation) {
        is ViewStackOp.Dismiss -> "--"
        is ViewStackOp.Pop -> "--"
        is ViewStackOp.Push -> "--"
        is ViewStackOp.Swap -> "-."
        is ViewStackOp.Reset -> "-."
        is ViewStackOp.Embed -> "=="
    }
    val arrowCharsSingle = when (operation) {
        is ViewStackOp.Dismiss -> "--"
        is ViewStackOp.Pop -> "--"
        is ViewStackOp.Push -> "--"
        is ViewStackOp.Swap -> "-.-"
        is ViewStackOp.Reset -> "-.-"
        is ViewStackOp.Embed -> "=="
    }
    if (passed.isEmpty())
        out.appendln("${nodeId[node.name]}${arrowCharsSingle}>${targetId};")
    else
        out.appendln("${nodeId[node.name]}${arrowChars} $passed ${arrowChars.reversed()}>${targetId};")
}
