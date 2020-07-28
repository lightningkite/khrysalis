package com.lightningkite.khrysalis.ios.layout

import com.lightningkite.khrysalis.generic.PartialTranslator
import com.lightningkite.khrysalis.swift.replacements.AttributeReplacement
import com.lightningkite.khrysalis.swift.replacements.Replacements
import com.lightningkite.khrysalis.swift.replacements.TemplatePart
import com.lightningkite.khrysalis.swift.replacements.TypeReplacement
import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.hashColorToUIColor
import com.lightningkite.khrysalis.utils.kabobCase
import java.io.File

class SwiftLayoutTranslator {

    var projectName: String? = null
    var styles: Styles = mapOf()
    var outFolder: File = File(".")
    val replacements = Replacements()

    fun getParentRule(node: XmlNode): TypeReplacement? = (replacements.types[node.name]
        ?: replacements.types["android.widget." + node.name]
        ?: replacements.types["android.view." + node.name])?.firstOrNull()

    fun getAttrRuleSequence(node: XmlNode.Attribute): Sequence<AttributeReplacement> {
        val t = getParentRule(node.parent) ?: return sequenceOf()
        return (sequenceOf(t.id) + t.xmlDefer.asSequence())
            .map { it + "." + node.type }
            .flatMap { replacements.attributes[it]?.asSequence() ?: sequenceOf() }
    }

    inner class ElementTranslator : PartialTranslator<SwiftLayoutEmitter, Unit, XmlNode, String>() {
        override fun getIdentifier(rule: XmlNode): String = rule.name
        override fun emitDefault(identifier: String, rule: XmlNode, out: SwiftLayoutEmitter): Unit {
            val typeRule = getParentRule(rule) ?: return
            typeRule.template.parts.joinToString("") {
                when(it) {
                    is TemplatePart.Text -> it.string
                    is TemplatePart.Import -> {
                        out.addImport(it)
                        ""
                    }
                    else -> ""
                }
            }
        }
    }

    inner class AttributeTranslator : PartialTranslator<SwiftLayoutEmitter, Unit, XmlNode.Attribute, String>() {
        override fun getIdentifier(rule: XmlNode.Attribute): String = rule.type
        override fun emitDefault(identifier: String, rule: XmlNode.Attribute, out: SwiftLayoutEmitter) {
            val value = rule.value
            getAttrRuleSequence(rule).firstOrNull()?.let {
                fun emit(value: String){
                    it.template.parts.joinToString("") {
                        when(it) {
                            is TemplatePart.Text -> it.string
                            TemplatePart.Receiver -> "view"
                            TemplatePart.DispatchReceiver -> "view"
                            TemplatePart.ExtensionReceiver -> "view"
                            TemplatePart.Value -> value
                            is TemplatePart.Import -> {
                                out.addImport(it)
                                ""
                            }
                            else -> ""
                        }
                    }
                }
                if(it.isColor && value.startsWith("@")){
                    out.appendln("applyColor(view, R.color.${value.substringAfter('/')}) { (c, s) in")
                    emit("c")
                    out.appendln("}")
                } else {
                    emit(when{
                        value.startsWith("@") -> {
                            val type = value.drop(1).substringAfter(':').substringBefore('/')
                            val path = value.substringAfter('/')
                            when (type) {
                                "mipmap", "drawable" -> "R.drawable.${path}"
                                "color" -> "R.color.${path}"
                                "string" -> "R.string.${path}"
                                else -> value
                            }
                        }
                        value.startsWith("#") -> {
                            value.hashColorToUIColor()
                        }
                        else -> value
                    })
                }
            }
        }
    }

    val element = ElementTranslator()
    val attribute = AttributeTranslator()

}

