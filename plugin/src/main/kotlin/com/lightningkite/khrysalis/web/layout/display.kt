package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.utils.kabobCase
import com.lightningkite.khrysalis.web.asCssColor
import java.io.File

internal fun HtmlTranslator.display() {
    element.handle("TextView") {
        out.name = "div"
        out.style["font-size"] = "12pt"
        if (rule.allAttributes["android:gravity"] == null) {
            out.style["text-align"] = "left"
        }
    }
    element.handle("Button") {
        out.name = "button"
    }
    element.handle("CompoundButton") {
        out.name = "button"
        out.classes += "butterfly-compound-off"
    }
    element.handle("com.rd.PageIndicatorView") {
        out.name = "div"
        out.classes += "butterfly-page-indicator"
    }
    element.handle("ImageView") {
        out.name = "img"
        when (rule.allAttributes["android:scaleType"]) {
            null -> {
                out.style["object-fit"] = "contain"
            }
            "center" -> {
                out.style["object-fit"] = "none"
            }
            "centerCrop" -> {
                out.style["object-fit"] = "cover"
            }
            "centerInside" -> {
                out.style["object-fit"] = "contain"
            }
            "fitCenter" -> {
                out.style["object-fit"] = "contain"
            }
            "fitEnd" -> {
                out.style["object-fit"] = "contain"
                out.style["object-position"] = "right"
            }
            "fitStart" -> {
                out.style["object-fit"] = "contain"
                out.style["object-position"] = "left"
            }
            "fitXY" -> {
                out.style["object-fit"] = "fill"
            }
            "matrix" -> {
                out.style["object-fit"] = "fill"
            }
        }
    }
    element.handle("ImageButton") {
        out.name = "button"
        out.classes.add("butterfly-single-item-container")
        val imageChild = ResultNode("img")
        out.contentNodes.add(imageChild)
    }

    element.handle("com.google.android.material.tabs.TabLayout") {
        out.name = "div"
        out.classes += "butterfly-tabs"
    }

    element.handle("com.google.android.gms.maps.MapView") {
        out.name = "div"
    }

    element.handle("com.lightningkite.butterfly.views.widget.VideoPlayer") {
        out.name = "video"
        out.attributes["controls"] = "true"
        out.attributes["crossorigin"] = "anonymous"
    }

    element.handle("com.lightningkite.butterfly.views.widget.HorizontalProgressBar") {
        out.name = "progress"
    }

    attribute.handle("android:src") {
        val target = if (out.name == "img") out
        else out.contentNodes
            .asSequence()
            .mapNotNull { it as? ResultNode }
            .find { it.name == "img" }
            ?: return@handle
        val value = rule.value
        when {
            value.startsWith("@") -> {
                val path = value.substringAfter('/')
                outFolder.resolve("src/images").walkTopDown().find { it.nameWithoutExtension == path.kabobCase() }
                    ?.let {
                        target.attributes["src"] = it.toRelativeString(outFolder.resolve("src"))
                    } ?: run {
                    println("WARNING: Failed to find $path in ${this@display.outFolder}/src/images")
                }
            }
            else -> {
            }
        }
    }
//    attribute.handle("android:tint") {
//        val value = rule.value
//        out.style["mask-image"] = "url(" + out.attributes["src"] + ")"
//        out.style["mask-repeat"] = "no-repeat"
//        out.style["mask-size"] = "100%"
//        out.name = "div"
//        out.attributes["src"]?.let { File(outFolder.resolve("src"), it) }?.let { imageUrl ->
//            val pair = measureImage(imageUrl)
//            out.style["width"] = pair.first
//            out.style["height"] = pair.second
//        }
//        value.asCssColor()?.let { out.style["background-color"] = it }
//    }
}
