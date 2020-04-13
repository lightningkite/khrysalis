package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.generic.PartialTranslator
import com.lightningkite.khrysalis.utils.AlignDirection
import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.horizontalGravity
import com.lightningkite.khrysalis.utils.kabobCase
import com.lightningkite.khrysalis.web.asCssColor
import com.lightningkite.khrysalis.web.asCssDimension
import java.io.File
import javax.imageio.ImageIO

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
        out.primary.contentNodes.add(
            when {
                value.startsWith("@") -> strings[value.substringAfter('/')] ?: "missing text"
                else -> value
            }
        )
    }
    attribute.handle("android:gravity") {
        horizontalGravity(rule.value)?.alignDirection?.let {
            out.primary.style["text-align"] = when (it) {
                AlignDirection.START -> "left"
                AlignDirection.CENTER -> "center"
                AlignDirection.END -> "right"
            }
        }
    }
    attribute.handle("android:textColor") {
        val value = rule.value
        value.asCssColor()?.let { out.primary.style["color"] = it }
    }
    attribute.handle("android:textSize") {
        val value = rule.value
        value.asCssDimension()?.let { out.primary.style["font-size"] = it }
    }
    attribute.handle("android:textAllCaps") {
        val value = rule.value
        out.primary.style["text-transform"] = if (value == "true") "uppercase" else "none"
    }
    attribute.handle("android:id") {
        val value = rule.value.substringAfter('/')
        out.primary.classes.add("id-${value.kabobCase()}")
    }

    fun PartialTranslator<ResultNode, Unit, XmlNode.Attribute, String>.Context.handleDrawable(direction: String, after: Boolean){
        out.primary.style["display"] = "flex"
        out.primary.style["flex-direction"] = direction
        out.primary.style["align-items"] = "center"
        val image = ResultNode("image")

        val value = rule.value
        var imageUrl: File? = null
        when {
            value.startsWith("@") -> {
                val path = value.substringAfter('/')
                outFolder.resolve("src/images").walkTopDown().find { it.nameWithoutExtension == path.kabobCase() }?.let {
                    imageUrl = it
                    image.attributes["src"] = imageUrl!!.toRelativeString(outFolder)
                } ?: run {
                    println("WARNING: Failed to find $path in ${outFolder}/src/images")
                }
            }
            else -> {}
        }
        rule.parent.allAttributes["android:drawableTint"]?.let { tint ->
            image.style["mask-image"] = "url(" + image.attributes["src"] + ")"
            image.style["mask-repeat"] = "no-repeat"
            image.style["mask-size"] = "100%"
            imageUrl?.let{ imageUrl ->
                val pair = measureImage(imageUrl)
                image.style["width"] = pair.first
                image.style["height"] = pair.second
            }
            image.name = "div"
            tint.asCssColor()?.let { image.style["background-color"] = it }
        }

        val ruleName = rule.parent.name
        out.primary.postProcess.add {
            println("Adding image to $ruleName which is a ${name}")
            if(after){
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
}
