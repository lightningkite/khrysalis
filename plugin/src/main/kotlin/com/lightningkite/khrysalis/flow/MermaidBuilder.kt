package com.lightningkite.khrysalis.flow

import java.lang.IllegalStateException

class MermaidBuilder(val out: Appendable, vertical: Boolean) {

    init {
        if (vertical) {
            out.appendLine("graph LR;")
        } else {
            out.appendLine("graph TD;")
        }
    }

    data class NodeStyle(val fill: Int, val stroke: Int, val strokeWidth: String) {
        fun emit(out: Appendable, forKey: String) {
            out.appendLine("style $forKey fill:${fill.colorString()},stroke:${stroke.colorString()},stroke-width:$strokeWidth;")
        }
    }

    enum class NodeShape(val start: String, val end: String) {
        Square("[", "]"),
        RoundSquare("(", ")"),
        Circle("((", "))"),
        Flag(">", "]"),
        Rhombus("{", "}"),
        Hexagon("{{", "{{"),
        Trapezoid("[/", "\\]"),
        TrapezoidAlt("[\\", "/]");

        fun make(key: String, name: String): String {
            return "$key$start\"$name\"$end"
        }
    }

    data class LinkStyle(val stroke: Int, val strokeWidth: String, val dashArray: List<Int>? = null) {
        fun emit(out: Appendable, forIndex: Int) {
            out.append("linkStyle ")
            out.append(forIndex.toString())
            out.append(" stroke:${stroke.colorString()},stroke-width:$strokeWidth")
            if (dashArray != null) {
                out.append("stroke-dasharray:${dashArray.joinToString()}")
            }
            out.appendLine(';')
        }
    }

    enum class LinkShape(val noLabel: String, val start: String, val end: String) {
        Arrow("-->", "-- ", " -->"),
        Line(" --- ", "-- ", " --"),
        DottedLine("-.-", "-. ", " .-"),
        DottedArrow("-.->", "-. ", " .->"),
        ThickArrow("==>", "== ", " ==>");

        fun make(left: String, right: String, content: String? = null): String {
            return if (content != null) "$left$start\"$content\"$end$right" else "$left$noLabel$right"
        }
    }

    var nodeIndex: Int = 0
    var linkIndex: Int = 0

    fun getShortIdentifier(index: Int): String {
        return when (index) {
            in Int.MIN_VALUE..-1 -> "UNK"
            in 0..25 -> ('A' + index).toString()
            in 26..675 -> ('A' + index / 26).toString() + ('A' + index % 26).toString()
            else -> ('A' + index / 676).toString() + ('A' + index / 26 % 26).toString() + ('A' + index % 26).toString()
        }
    }

    fun node(name: String, shape: NodeShape = NodeShape.RoundSquare, style: NodeStyle? = null): Int {
        val thisNodeIndex = nodeIndex++
        val thisNodeId = getShortIdentifier(thisNodeIndex)
        out.appendLine(shape.make(thisNodeId, name) + ";")
        style?.emit(out, thisNodeId)
        return thisNodeIndex
    }

    fun link(
        from: Int,
        to: Int,
        content: String? = null,
        shape: LinkShape = LinkShape.Arrow,
        style: LinkStyle? = null
    ): Int {
        val thisLinkIndex = linkIndex++
        out.appendLine(shape.make(getShortIdentifier(from), getShortIdentifier(to), content) + ";")
        style?.emit(out, thisLinkIndex)
        return thisLinkIndex
    }

    inline fun <T> subgraph(name: String, actions: () -> T): T {
        out.appendLine("subgraph $name;")
        val result = actions()
        out.appendLine("end;")
        return result
    }

    data class LinkInfo<T>(
        val toItem: T,
        val content: String? = null,
        val shape: LinkShape,
        val style: LinkStyle? = null
    )

    inner class UsingType<T> {
        val ids = HashMap<T, Int>()
        val atEnd = ArrayList<() -> Unit>()

        fun node(item: T, name: String, shape: NodeShape = NodeShape.RoundSquare, style: NodeStyle? = null): Int {
            ids[item]?.let { return it }
            val id = node(name, shape, style)
            ids[item] = id
            return id
        }

        fun link(
            from: T,
            to: T,
            content: String? = null,
            shape: LinkShape = LinkShape.Arrow,
            style: LinkStyle? = null
        ) {
            link(
                from = ids[from] ?: return,
                to = ids[to] ?: return,
                content = content,
                shape = shape,
                style = style
            )
        }

        fun link(
            from: T,
            to: T,
            content: String? = null,
            shape: LinkShape = LinkShape.Arrow,
            style: LinkStyle? = null,
            makeNode: (T) -> Int
        ) {
            link(
                from = ids.getOrPut(from) { makeNode(from) },
                to = ids.getOrPut(to) { makeNode(to) },
                content = content,
                shape = shape,
                style = style
            )
        }

        fun backlink(
            from: T,
            to: T,
            makeOriginalNode: (T) -> Int,
            makeBackNode: (T) -> Int,
            content: String? = null,
            shape: LinkShape = LinkShape.Arrow,
            style: LinkStyle? = null,
            backShape: LinkShape? = null,
            backStyle: LinkStyle? = null
        ) {
            ids[to]?.let { targetId ->
                val backNodeId = makeBackNode(to)
                link(
                    from = ids[from] ?: throw IllegalStateException("No id found for ${from}"),
                    to = backNodeId,
                    content = content,
                    shape = shape,
                    style = style
                )
                if (backShape != null) {
                    atEnd += {
                        link(
                            from = backNodeId,
                            to = targetId,
                            shape = backShape,
                            style = backStyle
                        )
                    }
                }
            } ?: run {
                val newTargetId = makeOriginalNode(to)
                ids[to] = newTargetId
                link(
                    from = ids[from] ?: throw IllegalStateException("No id found for ${from}"),
                    to = newTargetId,
                    content = content,
                    shape = shape,
                    style = style
                )
            }
        }

        fun forward(
            root: T,
            makeOriginalNode: (T) -> Int,
            makeBackNode: (T) -> Int,
            getLinks: (T) -> Sequence<LinkInfo<T>>,
            backShape: LinkShape? = null,
            backStyle: LinkStyle? = null
        ) {
            val toCheck = arrayListOf(root)
            val checked = hashSetOf(root)
            ids[root] = makeOriginalNode(root)
            while (toCheck.isNotEmpty()) {
                val next = toCheck.removeAt(0)
                for (link in getLinks(next)) {
                    backlink(
                        from = next,
                        to = link.toItem,
                        makeOriginalNode = makeOriginalNode,
                        makeBackNode = makeBackNode,
                        content = link.content,
                        shape = link.shape,
                        style = link.style,
                        backShape = backShape,
                        backStyle = backStyle
                    )
                    if (checked.add(link.toItem)) {
                        toCheck.add(link.toItem)
                    }
                }
            }
        }
    }

    fun <T> usingType(
        action: UsingType<T>.() -> Unit
    ) {
        UsingType<T>().apply(action).atEnd.forEach { it() }
    }
}

private fun Int.colorString() = "#" + this.toString(16).takeLast(6).padStart(6, '0')

inline fun Appendable.mermaid(vertical: Boolean = true, action: MermaidBuilder.() -> Unit) {
    MermaidBuilder(this, vertical).apply(action)
}
