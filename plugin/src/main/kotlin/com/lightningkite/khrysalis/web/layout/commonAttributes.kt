package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.generic.PartialTranslator
import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.web.asCssColor
import com.lightningkite.khrysalis.web.asCssDimension
import java.io.File

internal fun HtmlTranslator.commonAttributes() {
    attribute.handle("android:id") {
        val value = rule.value.substringAfter('/')
        out.primary.classes.add("id-${value}")
        out.primary.subtreeId = value
    }
    attribute.handle("tools:webCss") {
        val map = rule.value.split(';').associate { it.substringBefore(':').trim() to it.substringAfter(':').trim() }
        out.postProcess.add {
            style.putAll(map)
        }
    }
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
    attribute.handle("android:alpha") {
        out.style["opacity"] = rule.value
    }
    attribute.handle("android:elevation") {
        val elevationAmount = rule.value.asCssDimension()
        out.style["box-shadow"] = "0px $elevationAmount 5px 0px rgba(0,0,0,0.25)"
    }
    attribute.handle("android:text") {
        if (out.other["textAdded"] == true) return@handle
        out.other["textAdded"] = true
        val value = rule.value
        out.text.contentNodes.add(
            when {
                value.startsWith("@") -> strings[value.substringAfter('/')] ?: "missing text"
                else -> value.replace("\\n", "\n").replace("\\t", "\t")
            }
        )
    }
    attribute.handle("android:hint") {
        if (out.other["hintAdded"] == true) return@handle
        out.other["hintAdded"] = true
        val value = rule.value
        out.text.attributes["placeholder"] = when {
            value.startsWith("@") -> strings[value.substringAfter('/')] ?: "missing text"
            else -> value.replace("\\n", "\n").replace("\\t", "\t")
        }
    }
    attribute.handle("android:textOn") {
        defer("android:text")
    }
    attribute.handle("android:textOff") {
        defer("android:text")
    }
    attribute.handle("android:gravity") {
        horizontalGravity(rule.value)?.alignDirection?.let {
            out.text.style["text-align"] = when (it) {
                AlignDirection.START -> "left"
                AlignDirection.CENTER -> "center"
                AlignDirection.END -> "right"
            }
        }
    }
    attribute.handle("android:textColor") {
        val value = rule.value
        value.asCssColor()?.let { out.text.style["color"] = it }
    }
    attribute.handle("android:textSize") {
        val value = rule.value
        value.asCssDimension()?.let { out.text.style["font-size"] = it }
    }
    attribute.handle("android:textAllCaps") {
        val value = rule.value
        out.text.style["text-transform"] = if (value == "true") "uppercase" else "none"
    }

    fun PartialTranslator<ResultNode, Unit, XmlNode.Attribute, String>.Context.handleDrawable(
        direction: String,
        after: Boolean
    ) {
        out.containerNode.style["display"] = "flex"
        out.containerNode.style["flex-direction"] = direction
        out.containerNode.style["align-items"] = "center"
        val image = ResultNode("img")
        image.style["flex-grow"] = "0"

        val value = rule.value
        var imageUrl: File? = null
        when {
            value.startsWith("@") -> {
                val path = value.substringAfter('/')
                outFolder.resolve("src/images").walkTopDown().find { it.nameWithoutExtension == path.kabobCase() }
                    ?.let {
                        imageUrl = it
                        image.attributes["src"] = imageUrl!!.toRelativeString(outFolder.resolve("src"))
                    } ?: run {
                    println("WARNING: Failed to find $path in ${outFolder}/src/images")
                }
            }
            else -> {
            }
        }
//        rule.parent.allAttributes["android:drawableTint"]?.let { tint ->
//            image.style["mask-image"] = "url(" + image.attributes["src"] + ")"
//            image.style["mask-repeat"] = "no-repeat"
//            image.style["mask-size"] = "100%"
//            imageUrl?.let{ imageUrl ->
//                val pair = measureImage(imageUrl)
//                image.style["width"] = pair.first
//                image.style["height"] = pair.second
//            }
//            image.name = "div"
//            tint.asCssColor()?.let { image.style["background-color"] = it }
//        }

        rule.parent.allAttributes["android:drawablePadding"]?.asCssDimension()?.let { amount ->
            val side = when (direction) {
                "column" -> if (after) "top" else "bottom"
                "row" -> if (after) "left" else "right"
                else -> "left"
            }
            image.style["margin-$side"] = amount
        }

        val ruleName = rule.parent.name
        out.containerNode.postProcess.add {
            println("Adding image to $ruleName which is a ${name}")
            if (after) {
                contentNodes.add(image)
            } else {
                contentNodes.add(0, image)
            }
        }
    }

    attribute.handle("android:drawableLeft") { handleDrawable("row", false) }
    attribute.handle("android:drawableTop") { handleDrawable("column", false) }
    attribute.handle("android:drawableRight") { handleDrawable("row", true) }
    attribute.handle("android:drawableBottom") { handleDrawable("column", true) }

    attribute.handle("android:minWidth") {
        out.style["min-width"] = rule.value.asCssDimension() ?: "0"
    }
    attribute.handle("android:minHeight") {
        out.style["min-height"] = rule.value.asCssDimension() ?: "0"
    }
}
