package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.ios.layout.Styles
import com.lightningkite.khrysalis.utils.*
import java.lang.Appendable

class HtmlTranslator {

    var styles: Styles = mapOf()
    var dimensions: Map<String, String> = mapOf()
    var colors: Map<String, String> = mapOf()

    fun emitFile(root: XmlNode, out: Appendable) {
        out.appendln("<!DOCTYPE html>")
        out.appendln("<html>")
        out.appendln("  <head>")
        out.appendln("    <style>")
        out.append(coreCss)
        out.appendln("    </style>")
        out.appendln("  </head>")
        out.appendln("  <body>")
        convert(root).also { it.doPostProcess() }.emitHtml(out)
        out.appendln("  </body>")
        out.appendln("</html>")
    }

    val coreCss = """
      html,
      body {
        height: 100%;
        width: 100%;
        margin: 0;
        font-size: 1pt;
      }
      * {
        overflow-x: hidden;
        overflow-y: hidden;
      }
      .khrysalis-scroll-view {
        overflow-y: scroll;
        overflow-x: hidden;
      }
      .khrysalis-linear-layout {
        display: flex;
      }
      .khrysalis-linear-layout.khrysalis-vertical {
        flex-direction: column;
      }
      .khrysalis-linear-layout.khrysalis-horizontal {
        flex-direction: row;
      }
      .khrysalis-frame-layout {
        position: relative;
      }
      .khrysalis-frame-layout > .khrysalis-left {
        position: absolute;
        left: 0;
      }
      .khrysalis-frame-layout > .khrysalis-right {
        position: absolute;
        right: 0;
      }
      .khrysalis-frame-layout > .khrysalis-center-horizontal {
        position: absolute;
        transform: translateX(-50%);
        left: 50%;
      }
      .khrysalis-frame-layout > .khrysalis-center-vertical {
        position: absolute;
        transform: translateY(-50%);
        top: 50%;
      }
      .khrysalis-frame-layout > .khrysalis-top {
        position: absolute;
        top: 0;
      }
      .khrysalis-frame-layout > .khrysalis-bottom {
        position: absolute;
        bottom: 0;
      }
      .khrysalis-frame-layout > .khrysalis-center-vertical.khrysalis-center-horizontal {
        position: absolute;
        transform: translateX(-50%) translateY(-50%);
      }
      .khrysalis-text-container {
      }
      .khrysalis-text {
        font-size: 12rem;
      }
      .khrysalis-match-parent-width {
      }
      .khrysalis-match-parent-height {
      }
      .khrysalis-wrap-content-width {
      }
      .khrysalis-wrap-content-height {
      }
      .khrysalis-match-parent-height > * {
        /* Height should be calc(100% - this.topPadding - this.bottomPadding - this.topMargin - this.bottomMargin) */
      }
    """.trimIndent()

    val viewHandlers = HashMap<String, (XmlNode) -> Pair<ResultNode, ResultNode>>()
    val attributeHandlers = HashMap<String, Context.() -> Unit>()

    inner class Context {
        lateinit var out: ResultNode
        lateinit var inner: ResultNode
        lateinit var node: XmlNode
        lateinit var value: String
        val dimension: String?
            get() = when (value) {
                "match_parent" -> "100%"
                "wrap_content" -> null
                else -> {
                    if (value.startsWith("@")) {
                        dimensions[value.substringAfter('/')]
                    } else {
                        val number = value.filter { it.isDigit() || it == '.' }.toDouble()
                        when {
                            value.endsWith("dp") -> number.toString() + "px"
                            value.endsWith("sp") -> number.toString() + "px"
                            value.endsWith("px") -> number.toString() + "px"
                            else -> null
                        }
                    }
                }
            }
        val color: String?
            get() = when {
                value.startsWith("@color/") -> {
                    val colorName = value.removePrefix("@color/")
                    "ResourcesColors.${colorName.camelCase()}"
                }
                value.startsWith("@android:color/") -> {
                    val colorName = value.removePrefix("@android:color/")
                    "ResourcesColors.${colorName.camelCase()}"
                }
                value.startsWith("#") -> {
                    when (value.length - 1) {
                        3 -> "#" + value[1].toString().repeat(2) + value[2].toString().repeat(2) + value[3].toString()
                            .repeat(2)
                        4 -> "#" + value[2].toString().repeat(2) + value[3].toString().repeat(2) + value[4].toString()
                            .repeat(2) + value[1].toString().repeat(2)
                        6 -> value
                        8 -> "#" + value.drop(3).take(6) + value.drop(1).take(2)
                        else -> "#000000"
                    }
                }
                else -> null
            }
    }

    val reusedContext = Context()
    fun convert(node: XmlNode): ResultNode {
        val item =
            viewHandlers[node.name]?.invoke(node)
                ?: ResultNode("div").apply { classes.add("khrysalis-" + node.name.kabobCase()) }.both()
        reusedContext.node = node
        reusedContext.out = item.first
        reusedContext.inner = item.second
        for (att in node.allAttributes) {
            reusedContext.value = att.value
            attributeHandlers[att.key]?.invoke(reusedContext)
        }
        return item.first
    }

    init {
        viewHandlers["LinearLayout"] = {
            ResultNode("div").apply {
                classes.add("khrysalis-linear-layout")
                classes.add(if (it.allAttributes["android:orientation"] == "vertical") "khrysalis-vertical" else "khrysalis-horizontal")
                contentNodes.addAll(it.children.map {
                    convert(it)
                })
            }.both()
        }
        viewHandlers["FrameLayout"] = {
            ResultNode("div").apply {
                classes.add("khrysalis-frame-layout")
                contentNodes.addAll(it.children.map {
                    convert(it)
                })
            }.both()
        }
        viewHandlers["ScrollView"] = {
            ResultNode("div").apply {
                classes.add("khrysalis-scroll-view")
                contentNodes.addAll(it.children.map {
                    convert(it)
                })
            }.both()
        }
        viewHandlers["TextView"] = {
            val outer = ResultNode("div")
            val inner = ResultNode("div")
            outer.contentNodes += inner
            outer.classes.add("khrysalis-text-container")
            inner.classes.add("khrysalis-text")
            outer to inner
        }
        viewHandlers["Button"] = {
            ResultNode("button").both()
        }
        attributeHandlers["android:text"] = { inner.contentNodes.add(value) }
        attributeHandlers["android:textColor"] = { inner.style["color"] = color ?: "black" }
        attributeHandlers["android:background"] = {
            color?.let {
                out.style["background-color"] = it
            }
        }
        attributeHandlers["android:layout_width"] = {
            if (value == "match_parent") {
                out.classes.add("khrysalis-match-parent-width")
                out.postProcess.add {
                    val parts = ArrayList<String>()
                    style["padding"]?.let {
                        parts.add("($it * 2)")
                    } ?: run {
                        style["padding-left"]?.let {
                            parts.add(it)
                        }
                        style["padding-right"]?.let {
                            parts.add(it)
                        }
                    }
                    style["margin"]?.let {
                        parts.add("($it * 2)")
                    } ?: run {
                        style["margin-left"]?.let {
                            parts.add(it)
                        }
                        style["margin-right"]?.let {
                            parts.add(it)
                        }
                    }
                    if(parts.isEmpty()){
                        style["width"] = "100%"
                    } else {
                        style["width"] = "calc(100% - (${parts.joinToString(" + ")}))"
                    }
                }
            } else {
                dimension?.let { out.style["width"] = it }
            }
        }
        attributeHandlers["android:layout_height"] = {
            if (value == "match_parent") {
                out.classes.add("khrysalis-match-parent-height")
                out.postProcess.add {
                    val parts = ArrayList<String>()
                    style["padding"]?.let {
                        parts.add("($it * 2)")
                    } ?: run {
                        style["padding-top"]?.let {
                            parts.add(it)
                        }
                        style["padding-bottom"]?.let {
                            parts.add(it)
                        }
                    }
                    style["margin"]?.let {
                        parts.add("($it * 2)")
                    } ?: run {
                        style["margin-top"]?.let {
                            parts.add(it)
                        }
                        style["margin-bottom"]?.let {
                            parts.add(it)
                        }
                    }
                    if(parts.isEmpty()){
                        style["height"] = "100%"
                    } else {
                        style["height"] = "calc(100% - (${parts.joinToString(" + ")}))"
                    }
                }
            } else {
                dimension?.let { out.style["height"] = it }
            }
        }
        attributeHandlers["android:layout_margin"] = {
            dimension?.let { out.style["margin"] = it }
        }
        attributeHandlers["android:padding"] = {
            dimension?.let { out.style["padding"] = it }
        }
        attributeHandlers["android:gravity"] = {
            value.split('|').map { it.replace('_', '-') }.forEach {
                if (it == "center") {
                    inner.classes.add("khrysalis-center-vertical")
                    inner.classes.add("khrysalis-center-horizontal")
                } else inner.classes.add("khrysalis-$it")
            }
        }
        attributeHandlers["android:layout_gravity"] = {
            value.split('|').map { it.replace('_', '-') }.forEach {
                if (it == "center") {
                    out.classes.add("khrysalis-center-vertical")
                    out.classes.add("khrysalis-center-horizontal")
                } else out.classes.add("khrysalis-$it")
            }
        }
        attributeHandlers["android:layout_weight"] = {
            out.style["flex-grow"] = value
        }
    }
}

fun <T> T.both(): Pair<T, T> = Pair(this, this)
