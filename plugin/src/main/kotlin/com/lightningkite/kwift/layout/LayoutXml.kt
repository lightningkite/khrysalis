package com.lightningkite.kwift.layout

import com.lightningkite.kwift.utils.XmlNode
import com.lightningkite.kwift.utils.camelCase
import java.io.File
import java.lang.StringBuilder

typealias Styles = Map<String, Map<String, String>>

fun File.translateLayoutXml(styles: Styles, converter: LayoutConverter = LayoutConverter.normal): String {

    val appendable = StringBuilder()
    val conversion = OngoingLayoutConversion(
        appendable = appendable,
        layoutsDirectory = this.parentFile,
        styles = styles,
        converter = converter
    )
    val root = XmlNode.read(this, styles)

    val name = this.nameWithoutExtension.camelCase().capitalize()

    with(appendable) {
        appendln("//")
        appendln("// ${name}Xml.swift")
        appendln("// Created by Kwift XML")
        appendln("//")
        appendln("")
        for(import in converter.imports) {
            appendln("import $import")
        }
        appendln("")
        appendln("class ${name}Xml {")
        appendln("")
        appendln("    unowned var xmlRoot: UIView!")

        appendln("    func setup(_ dependency: ViewDependency) -> UIView {")
        append("        let view = ")
        conversion.construct(root)
        appendln()
        conversion.writeSetup(root)
        appendln("        xmlRoot = view")
        appendln("        return view")
        appendln("    }")
        appendln("    ")
        conversion.sublayouts.entries.forEach {
            appendln("let ${it.key}: ${it.value} = ${it.value}()")
        }
        conversion.bindings.entries.forEach {
            appendln("unowned var ${it.key}: ${it.value}!")
        }
        appendln("    ")
        appendln("}")
    }

    return appendable.toString()
}
