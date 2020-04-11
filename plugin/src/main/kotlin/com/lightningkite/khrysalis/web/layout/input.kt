package com.lightningkite.khrysalis.web.layout

internal fun HtmlTranslator.input() {
    element.handle("EditText"){
        out.name = "input"
        out.attributes["type"] = "text"
        out.style["font-size"] = "12pt"
    }
}
