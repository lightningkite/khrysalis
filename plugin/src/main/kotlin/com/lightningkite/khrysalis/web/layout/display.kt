package com.lightningkite.khrysalis.web.layout

internal fun HtmlTranslator.display() {
    element.handle("TextView"){
        out.name = "div"
        out.style["font-size"] = "12pt"
    }
    element.handle("Button"){
        out.name = "button"
    }
    element.handle("ImageView") {
        out.name = "image"
    }
    attribute.handle("android:src", condition = { rule.parent.name == "ImageView" }){
        val value = rule.value
        out.attributes["src"] = when {
            value.startsWith("@") -> "/res/" + value.substringAfter('/') + ".png"
            else -> ""
        }
    }
}
