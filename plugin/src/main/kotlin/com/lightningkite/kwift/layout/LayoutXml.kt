package com.lightningkite.kwift.layout

import com.lightningkite.kwift.utils.XmlNode
import com.lightningkite.kwift.swift.retabSwift
import com.lightningkite.kwift.utils.camelCase
import java.io.File
import java.lang.StringBuilder

typealias Styles = Map<String, Map<String, String>>

fun File.translateLayoutXml(styles: Styles, converter: LayoutConverter = LayoutConverter.normal): String {

    val appendable = StringBuilder()
    val conversion = OngoingLayoutConversion(
        appendable = appendable,
        resourcesDirectory = this.parentFile,
        styles = styles,
        converter = converter
    )
    val root = XmlNode.read(this, styles)
    conversion.writeSetup(root)

    val name = this.nameWithoutExtension.camelCase().capitalize()
    return """
        //
        // ${name}Xml.swift
        // Created by Kwift XML
        //

        import UIKit
        import FlexLayout
        import PinLayout
        import Kwift

        class ${name}Xml {

            ${conversion.bindings.entries.joinToString("\n") {
        if(it.value.endsWith("!")) {
            "var ${it.key}: ${it.value}"
        } else {
            "unowned var ${it.key}: ${it.value}!"
        }
            }}
            unowned var xmlRoot: UIView!

            func setup(_ dependency: ViewDependency) -> UIView {
                let view = ${conversion.construct(root)}
                $appendable
                xmlRoot = view
                return view
            }
            
        }
    """.trimIndent().retabSwift()
}
