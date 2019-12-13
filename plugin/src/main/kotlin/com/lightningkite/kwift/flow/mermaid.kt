package com.lightningkite.kwift.flow

import java.io.File


internal fun groupedGraph(
    outputFolder: File,
    nodes: Map<String, ViewNode>
) {
    println("Making groupedGraph")
    outputFolder.resolve("flow-grouped.mmd").bufferedWriter().use { out ->
        val groupedNodes = nodes
            .entries
            .groupBy {
                val belongsTo = it.value.belongsToStacks(nodes)
                if (belongsTo.size == 1) belongsTo.first()
                else ""
            }
            .mapValues { it.value.sortedBy { it.value.depth } }
        val nodeIds = HashMap<String, Int>()
        groupedNodes
            .flatMap { it.value.map { it.value } }
            .forEach {
                println("Mismatch: $it")
            }
        out.mermaid {
            for ((group, nodes) in groupedNodes) {
                if (!group.isBlank() && group != "stack") {
                    subgraph(group) {
                        for (it in nodes) {
                            nodeIds[it.key] = node(it.key)
                        }
                    }
                } else {
                    for (it in nodes) {
                        nodeIds[it.key] = node(it.key)
                    }
                }
            }
            for (entry in groupedNodes.flatMap { it.value }) {
                for (operation in entry.value.operations) {
                    link(
                        from = nodeIds[entry.key] ?: continue,
                        to = nodeIds[operation.viewName] ?: continue,
                        shape = operation.linkShape(),
                        content = nodes[operation.viewName]
                            ?.totalRequires(nodes)
                            ?.joinToString { it.name }
                            ?.replace('[', '<')
                            ?.replace(']', '>')
                            ?.takeUnless { it.isEmpty() }
                    )
                }
            }
        }
    }
}

internal fun sortedGraph(
    outputFolder: File,
    nodes: Map<String, ViewNode>
) {
    println("Making sortedGraph")
    outputFolder.resolve("flow-sorted.mmd").bufferedWriter().use { out ->
        out.mermaid {
            usingType<ViewNode> {
                val sortedNodes = nodes.values
                    .sortedBy { it.depth }
                sortedNodes.forEach {
                    node(it, it.name)
                }
                sortedNodes.forEach {
                    for (op in it.operations) {
                        val target = op.viewName?.let { nodes[it] } ?: continue
                        link(
                            from = it,
                            to = target,
                            content = passingInfo(target, nodes),
                            shape = op.linkShape()
                        )
                    }
                }
            }
        }
    }
}

internal fun sortedNoReversalsGraph(
    outputFolder: File,
    nodes: Map<String, ViewNode>
) {
    println("Making flow data")
    outputFolder.resolve("flow-data.mmd").bufferedWriter().use { out ->
        out.mermaid {
            usingType<ViewNode> {
                forward(
                    root = ViewNode.root(nodes)!!,
                    makeOriginalNode = { node(it.name) },
                    makeBackNode = { node(it.name, shape = MermaidBuilder.NodeShape.Flag) },
                    getLinks = {
                        it.operations.asSequence().mapNotNull {
                            val target = nodes[it.viewName ?: ""] ?: return@mapNotNull null
                            MermaidBuilder.LinkInfo(
                                toItem = target,
                                content = passingInfo(target, nodes),
                                shape = it.linkShape()
                            )
                        }
                    }
                )
            }
        }
    }
    println("Making flow data")
    outputFolder.resolve("flow-stack.mmd").bufferedWriter().use { out ->
        out.mermaid {
            usingType<ViewNode> {
                forward(
                    root = ViewNode.root(nodes)!!,
                    makeOriginalNode = { node(it.name) },
                    makeBackNode = { node(it.name, shape = MermaidBuilder.NodeShape.Flag) },
                    getLinks = {
                        it.operations.asSequence().mapNotNull {
                            val target = nodes[it.viewName ?: ""] ?: return@mapNotNull null
                            MermaidBuilder.LinkInfo(
                                toItem = target,
                                content = it.stack?.takeUnless { it == "stack" },
                                shape = it.linkShape()
                            )
                        }
                    }
                )
            }
        }
    }
}

internal fun partialGraphs(
    outputFolder: File,
    nodes: Map<String, ViewNode>
) {
    println("Making partialGraphs")
    val groupedNodes = nodes.values.asSequence().flatMap { it.operations.asSequence().mapNotNull { it.stack } }.toSet()
        .associate { stack ->
            stack to nodes.filter { stack in it.value.belongsToStacks(nodes) }
        }.mapValues { it.value.entries.sortedBy { it.value.depth } }
        .mapValues { it.value.sortedBy { it.value.depth } }
    for ((group, values) in groupedNodes) {
        println("Making partialGraph for $group")
        if (group.isEmpty() || group == "stack") continue
        outputFolder.resolve("flow-partial-$group.mmd").bufferedWriter().use { out ->
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
            out.mermaid {
                usingType<ViewNode> {
                    beforeNodes.forEach {
                        node(it, it.name)
                    }
                    subgraph(group) {
                        internalNodes.forEach {
                            node(it, it.name)
                        }
                    }
                    afterNodes.forEach {
                        node(it, it.name)
                    }
                    (beforeNodes).forEach { node ->
                        for (operation in node.operations) {
                            if (operation.viewName in subsetNodeNames && operation.stack == group) {
                                val operationNode = nodes[operation.viewName ?: continue] ?: continue
                                link(
                                    from = node,
                                    to = operationNode,
                                    content = passingInfo(node, nodes),
                                    shape = operation.linkShape()
                                )
                            }
                        }
                    }
                    internalNodes.forEach { node ->
                        for (operation in node.operations) {
                            val operationNode = nodes[operation.viewName ?: continue] ?: continue
                            link(
                                from = node,
                                to = operationNode,
                                content = passingInfo(node, nodes),
                                shape = operation.linkShape()
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun ViewStackOp.linkShape(): MermaidBuilder.LinkShape {
    return when (this) {
        is ViewStackOp.Dismiss -> MermaidBuilder.LinkShape.Line
        is ViewStackOp.Pop -> MermaidBuilder.LinkShape.Line
        is ViewStackOp.Push -> MermaidBuilder.LinkShape.Arrow
        is ViewStackOp.Swap -> MermaidBuilder.LinkShape.DottedArrow
        is ViewStackOp.Reset -> MermaidBuilder.LinkShape.DottedLine
        is ViewStackOp.Embed -> MermaidBuilder.LinkShape.ThickArrow
        is ViewStackOp.PopTo -> MermaidBuilder.LinkShape.DottedArrow
    }
}

private fun passingInfo(
    target: ViewNode,
    nodes: Map<String, ViewNode>
): String? {
    return target.totalRequires(nodes)
        .joinToString { it.name }
        .replace('[', '<')
        .replace(']', '>')
        .takeUnless { it.isEmpty() }
}
