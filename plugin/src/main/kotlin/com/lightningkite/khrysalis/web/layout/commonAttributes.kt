package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.utils.kabobCase
import com.lightningkite.khrysalis.web.asCssColor
import com.lightningkite.khrysalis.web.asCssDimension

internal fun HtmlTranslator.commonAttributes() {

    attribute.handle("android:background") {
        val value = rule.value
        when {
            value.startsWith("@") -> {
                val type = value.drop(1).substringAfter(':').substringBefore('/')
                val path = value.substringAfter('/').kabobCase()
                when (type) {
                    "mipmap", "drawable" -> {
                        out.classes.add("drawable-$path")
                    }
                    "color" -> {
                        out.style["background-color"] = value.asCssColor() ?: "#000"
                    }
                }
            }
            value.startsWith("#") -> {
                out.style["background-color"] = value
            }
        }
    }
    attribute.handle("android:text") {
        val value = rule.value
        out.contentNodes.add(
            when {
                value.startsWith("@") -> strings[value.substringAfter('/')] ?: "missing text"
                else -> value
            }
        )
    }
    attribute.handle("android:textColor") {
        val value = rule.value
        value.asCssColor()?.let { out.style["color"] = it }
    }
    attribute.handle("android:textSize") {
        val value = rule.value
        value.asCssDimension()?.let { out.style["font-size"] = it }
    }
    attribute.handle("android:textAllCaps") {
        val value = rule.value
        out.style["text-transform"] = if(value == "true") "uppercase" else "none"
    }
    attribute.handle("android:id") {
        val value = rule.value.substringAfter('/')
        out.classes.add("id-${value.kabobCase()}")
    }
}
