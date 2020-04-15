package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.utils.forEachBetween
import org.apache.commons.lang3.StringEscapeUtils.escapeHtml4
import java.lang.Appendable
import java.net.URLEncoder

class ResultNode(
    var name: String = "div"
) {
    var parent: ResultNode? = null
    val other = HashMap<String, Any>()
    val classes = HashSet<String>()
    val stylesheet = ArrayList<String>()
    val style = HashMap<String, String>()
    val attributes = HashMap<String, String>()
    val contentNodes = ArrayList<Any>()
    val postProcess = ArrayList<ResultNode.() -> Unit>()

    var primary: ResultNode = this
    var containerNode: ResultNode = this

    fun doPostProcess(){
        postProcess.forEach { it.invoke(this) }
        postProcess.clear()
        for(child in contentNodes.toList()){
            if(child is ResultNode){
                child.doPostProcess()
            }
        }
    }

    fun emitHtml(out: Appendable) {

        out.append("<$name ")
        if (classes.isNotEmpty()) {
            out.append("class=\"")
            classes.forEachBetween(
                forItem = { value ->
                    out.append(value)
                },
                between = { out.append(" ") }
            )
            out.append("\"")
        }
        if (style.isNotEmpty()) {
            out.append("style=\"")
            style.entries.forEachBetween(
                forItem = { (key, value) ->
                    out.append(key)
                    out.append(":")
                    out.append(value)
                },
                between = { out.append(";") }
            )
            out.append("\"")
        }
        if (attributes.isNotEmpty()) {
            for ((key, value) in attributes) {
                out.append(key)
                out.append("=\"")
                out.append(value.replace("\"", "&quot;"))
                out.append("\" ")
            }
        }
        if (contentNodes.isNotEmpty()) {
            out.append(">")
            for (node in contentNodes) {
                when (node) {
                    is String -> out.append(node.let { escapeHtml4(it) }.replace("\n", "<br>"))
                    is ResultNode -> node.emitHtml(out)
                }
            }
            out.append("</$name>")
        } else {
            out.append("></$name>")
        }
    }
}
