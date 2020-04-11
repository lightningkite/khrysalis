package com.lightningkite.khrysalis.web.layout.drawables

import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.attributeAsBoolean
import com.lightningkite.khrysalis.web.layout.HtmlTranslator
import java.lang.Appendable


fun convertSelectorDrawable(selectors: String, node: XmlNode, out: Appendable) {
    node.children.forEach { subnode ->
        var conditions = ""
        subnode.attributeAsBoolean("android:state_enabled")?.let {
            conditions += if(it) ":enabled" else ":disabled"
        }
        subnode.attributeAsBoolean("android:state_pressed")?.let {
            conditions += if(it) ":active:hover" else ":not(:active:hover)"
        }
        subnode.attributeAsBoolean("android:state_selected")?.let {
            conditions += if(it) ":checked" else ":not(:checked)"
        }
        subnode.attributeAsBoolean("android:state_focused")?.let {
            conditions += if(it) ":focus" else ":not(:focus)"
        }
        subnode.attributeAsBoolean("android:state_checked")?.let {
            conditions += if(it) ":checked" else ":not(:checked)"
        }

        subnode.allAttributes["android:drawable"]?.let {
            if (conditions.isEmpty()) {
                out.appendln("$selectors {")
            } else {
                out.appendln("$selectors$conditions {")
            }
            out.appendln("@extend drawable-${it.substringAfter('/')}")
            out.appendln("}")
        } ?: run {
            subnode.children.firstOrNull()?.let {
                convertDrawableXml(selectors + conditions, it, out)
            }
        }
    }
}
