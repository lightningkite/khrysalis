package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.generic.PartialTranslator
import com.lightningkite.khrysalis.utils.*

internal fun HtmlTranslator2.commonAttributes() {

    attribute.handle("android:background") {
        val value = rule.value
        when {
            value.startsWith("@") -> {
                val type = value.drop(1).substringAfter(':').substringBefore('/')
                val path = value.substringAfter('/')
                when (type) {
                    "drawable" -> {
                    }
                    "mipmap" -> {
                    }
                    "color" -> {
                        out.style["background-color"] = colors[path] ?: "black"
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
}
