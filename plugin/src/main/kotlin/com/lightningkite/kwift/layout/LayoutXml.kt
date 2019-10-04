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
    val vars = conversion.bindings.entries.joinToString("\n") {
        "weak var ${it.key}: ${it.value}!"
    }
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

            $vars

            func setup(_ dependency: ViewDependency) -> UIView {
                return $appendable
            }
            
        }
    """.trimIndent().retabSwift()
}
