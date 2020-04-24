package com.lightningkite.khrysalis.web.layout.drawables

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.web.layout.HtmlTranslator
import java.io.File
import java.lang.Appendable


fun convertBitmapDrawable(webDrawablesFolder: File, currentDrawable: String, selectors: String, node: XmlNode, out: Appendable) {
    out.appendln("$selectors {")
    out.appendln("@extend drawable-${node.directAttributes["android:src"]?.substringAfter('/')?.kabobCase()}")
    out.appendln("}")
}
