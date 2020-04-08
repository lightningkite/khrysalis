package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.generic.PartialTranslator
import com.lightningkite.khrysalis.ios.layout.Styles
import com.lightningkite.khrysalis.utils.*
import java.lang.Appendable

class HtmlTranslator2 {

    var colorSets = HashSet<String>()
    var colors = hashMapOf<String, String>("white" to "#FFF", "black" to "#000")
    var strings = hashMapOf<String, String>()
    var styles: Styles = mapOf()

    inner class ElementTranslator : PartialTranslator<ResultNode, Unit, XmlNode, String>() {
        override fun getIdentifier(rule: XmlNode): String = rule.name
        override fun emitDefault(identifier: String, rule: XmlNode, out: ResultNode): Unit {
            out.classes.add("khrysalis-" + rule.name.kabobCase())
        }

        override fun translate(identifier: String, rule: XmlNode, out: ResultNode, afterPriority: Int) {
            super.translate(identifier, rule, out, afterPriority)
            rule.parts.forEach { attribute.translate(it, out) }
        }
    }

    inner class AttributeTranslator : PartialTranslator<ResultNode, Unit, XmlNode.Attribute, String>() {
        override fun getIdentifier(rule: XmlNode.Attribute): String = rule.type
        override fun emitDefault(identifier: String, rule: XmlNode.Attribute, out: ResultNode) {}
    }

    val element = ElementTranslator()
    val attribute = AttributeTranslator()

    fun convertDimension(value: String): String {
        val numerical = value.filter { it.isDigit() || it == '.' }
        return when {
            value.startsWith("@") -> "0px"
            value.endsWith("px") -> numerical + "px"
            value.endsWith("dp") -> numerical + "rem"
            value.endsWith("dip") -> numerical + "rem"
            value.endsWith("sp") -> numerical + "rem"
            else -> "0px"
        }
    }

    init {
        layout()
        commonAttributes()
        display()
    }


    fun emitFile(root: XmlNode, out: Appendable) {
        out.appendln("<!DOCTYPE html>")
        out.appendln("<html>")
        out.appendln("  <head>")
        out.appendln("    <style>")
        out.append(coreCss)
        out.appendln("    </style>")
        out.appendln("  </head>")
        out.appendln("  <body>")
        ResultNode().apply { element.translate(root, this) }.also { it.doPostProcess() }.emitHtml(out)
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
      body > * {
        height: 100%;
        width: 100%;
      }
      * {
        overflow-x: hidden;
        overflow-y: hidden;
        box-sizing: border-box;
      }
      .khrysalis-scroll-view {
        overflow-y: scroll;
        overflow-x: hidden;
      }
      .khrysalis-linear-layout {
        display: flex;
      }
      .khrysalis-frame-layout {
        position: relative;
      }
      .khrysalis-text-container {
      }
      .khrysalis-text {
        font-size: 12rem;
      }
    """.trimIndent()
}
