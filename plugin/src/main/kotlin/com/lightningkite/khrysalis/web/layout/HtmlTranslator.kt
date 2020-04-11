package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.generic.PartialTranslator
import com.lightningkite.khrysalis.ios.layout.Styles
import com.lightningkite.khrysalis.utils.*
import java.lang.Appendable

class HtmlTranslator {

    var colorSets = HashSet<String>()
    var colors = mapOf<String, String>("white" to "#FFF", "black" to "#000")
    var strings = mapOf<String, String>()
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

    init {
        layout()
        commonAttributes()
        display()
        input()
    }


    fun emitFile(root: XmlNode, out: Appendable) {
        ResultNode().apply { element.translate(root, this) }.also { it.doPostProcess() }.emitHtml(out)
    }
}
