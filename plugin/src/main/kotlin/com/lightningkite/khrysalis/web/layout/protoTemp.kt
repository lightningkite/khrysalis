package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.generic.PartialTranslator
import com.lightningkite.khrysalis.utils.XmlNode
import java.util.*

fun String.calcDest(): String = this.trim().removePrefix("@layout/")

fun HtmlTranslator.protoTemp() {

    element.handle("com.lightningkite.khrysalis.views.android.SwapView") {
        out.name = "div"
        out.style["position"] = "relative"
        val stackDefault = rule.allAttributes["tools:stackDefault"]?.calcDest()
        rule.allAttributes["tools:stackId"]?.trim()?.let {
            out.attributes["id"] = "view_$it"
            val stackDefaultJs = if (stackDefault != null) "'$stackDefault'" else "null"
            out.contentNodes.add(ResultNode("script").apply {
                contentNodes.add("""prototypeSwapViewSetup(document.getElementById('view_$it'), '$it', $stackDefaultJs)""")
            })
        }
    }
    fun PartialTranslator<ResultNode, Unit, XmlNode.Attribute, String>.Context.handleLink(action: String) {
        val dest = rule.value.calcDest()
        val onStack = rule.parent.allAttributes["tools:onStack"]?.trim()
        if (onStack != null) {
            out.attributes["onclick"] = """$action(this, '$onStack', '$dest')"""
        } else {
            out.attributes["onclick"] = """$action(this, null, '$dest')"""
        }
    }

    fun PartialTranslator<ResultNode, Unit, XmlNode.Attribute, String>.Context.handleAction(action: String) {
        val onStack = rule.parent.allAttributes["tools:onStack"]?.trim()
        if (onStack != null) {
            out.attributes["onclick"] = """$action(this, '$onStack')"""
        } else {
            out.attributes["onclick"] = """$action(this, null)"""
        }
    }
    attribute.handle("tools:goTo") { handleLink("prototypePush") }
    attribute.handle("tools:swap") { handleLink("prototypeSwap") }
    attribute.handle("tools:reset") { handleLink("prototypeReset") }
    attribute.handle("tools:pop") { handleAction("prototypePop") }
    attribute.handle("tools:dismiss") { handleAction("prototypeDismiss") }

    attribute.handle("tools:listitem") {
        out.other.getOrPut("listid") {
            val id = UUID.randomUUID().toString().replace("-", "")
            out.attributes["id"] = "list_$id"
            out.contentNodes.add(ResultNode("script").apply {
                contentNodes.add("""prototypePopulateList(document.getElementById('list_$id'), '${rule.value.calcDest()}')""")
            })
            id
        }
    }

    attribute.handle("tools:text") { defer("android:text") }
    attribute.handle("tools:src") { defer("android:src") }
}
/*
prototypeSetup
prototypePush
prototypeSwap
prototypeReset
prototypePop
prototypeDismiss
 */
