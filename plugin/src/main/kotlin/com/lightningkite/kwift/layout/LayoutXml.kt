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
    conversion.write(XmlNode.read(this, styles))
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
                "unowned var ${it.key}: ${it.value}!"
            }}
            unowned var xmlRoot: UIView!

            func setup(_ dependency: ViewDependency) -> UIView {
                let result = $appendable
                xmlRoot = result
                return result
            }
            
        }
    """.trimIndent().retabSwift()
}

/*
        class ${name}Xml {

            ${conversion.bindings.entries.joinToString("\n") {
                "unowned var ${it.key}_raw: ${it.value}?"
            }}
            ${conversion.bindings.entries.joinToString("\n") {
                "var ${it.key}: ${it.value} { return ${it.key}_raw! }"
            }}

            unowned var xmlRoot_raw: UIView?
            var xmlRoot: UIView { return xmlRoot_raw! }

            func setup(_ dependency: ViewDependency) -> UIView {
                let result = $appendable
                xmlRoot_raw = result
                return result
            }

        }*/
