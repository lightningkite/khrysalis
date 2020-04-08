package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.generic.PartialTranslator
import com.lightningkite.khrysalis.utils.*

internal fun HtmlTranslator2.display() {
    element.handle("TextView"){
        out.name = "div"
        out.classes.add("khrysalis-text")
    }
    element.handle("Button"){
        out.name = "button"
    }
}
