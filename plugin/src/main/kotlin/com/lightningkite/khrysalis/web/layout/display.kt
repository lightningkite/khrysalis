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
        out.classes += "khrysalis-compound-off"
    }
    element.handle("com.rd.PageIndicatorView") {
        out.name = "div"
        out.classes += "khrysalis-page-indicator"
    }
    element.handle("ImageView") {
        out.name = "image"
    }
    element.handle("ImageButton") {
        out.name = "button"
        val imageChild = ResultNode("image")
        element.translate("ImageView", rule, imageChild)
        out.contentNodes.add(imageChild)
    }

    element.handle("com.lightningkite.khrysalis.views.android.SwapView"){
        out.name = "div"
        out.classes += "khrysalis-swap"
    }

    element.handle("ViewFlipper"){
        out.name = "div"
        out.classes += "khrysalis-flip"
    }

    element.handle("androidx.viewpager.widget.ViewPager"){
        out.name = "div"
        out.classes += "khrysalis-pager"
        out.contentNodes += ResultNode("button").apply {
            classes += "khrysalis-pager-left"
        }
        out.contentNodes += ResultNode("button").apply {
            classes += "khrysalis-pager-right"
        }
    }

    element.handle("com.google.android.material.tabs.TabLayout"){
        out.name = "div"
        out.classes += "khrysalis-tabs"
    }

    attribute.handle("android:src", condition = { out.name == "image" }) {
        val value = rule.value
        when {
            value.startsWith("@") -> {
                val path = value.substringAfter('/')
                outFolder.resolve("src/images").walkTopDown().find { it.nameWithoutExtension == path.kabobCase() }
                    ?.let {
                        out.attributes["src"] = it.toRelativeString(outFolder)
                    } ?: run {
                    println("WARNING: Failed to find $path in ${this@display.outFolder}/src/images")
                }
            }
            else -> {
            }
        }
    }
    attribute.handle("android:tint") {
        val value = rule.value
        out.style["mask-image"] = "url(" + out.attributes["src"] + ")"
        out.style["mask-repeat"] = "no-repeat"
        out.style["mask-size"] = "100%"
        out.name = "div"
        out.attributes["src"]?.let { File(outFolder, it) }?.let { imageUrl ->
            val pair = measureImage(imageUrl)
            out.style["width"] = pair.first
            out.style["height"] = pair.second
        }
        value.asCssColor()?.let { out.style["background-color"] = it }
    }
}
