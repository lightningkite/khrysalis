package com.lightningkite.khrysalis.ios.layout

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.android.layout.AndroidLayoutFile
import com.lightningkite.khrysalis.swift.safeSwiftIdentifier
import com.lightningkite.khrysalis.typescript.safeJsIdentifier
import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.camelCase
import java.io.File
import java.lang.StringBuilder

typealias Styles = Map<String, Map<String, String>>

internal fun translateLayoutXml(
    android: AndroidLayoutFile,
    styles: Styles,
    converter: LayoutConverter = LayoutConverter.normal
): String {

    val appendable = StringBuilder()
    val conversion = OngoingLayoutConversion(
        appendable = appendable,
        resourcesDirectory = android.files.first().parentFile.parentFile,
        styles = styles,
        converter = converter
    )

    val name = android.name

    with(appendable) {
        appendln("//")
        appendln("// ${name}Xml.swift")
        appendln("// Created by Khrysalis XML")
        appendln("//")
        appendln("")
        for (import in converter.imports) {
            appendln("import $import")
        }
        appendln("")
        appendln("public class ${name}Xml {")
        appendln("")
        appendln("    public unowned var xmlRoot: UIView!")

        appendln("    private var _layoutTests: Array<()->Bool> = []")
        appendln("    private func pickLayout(test: @escaping()->Bool) -> Bool {")
        appendln("        _layoutTests.append(test)")
        appendln("        return test()")
        appendln("    }")
        appendln("    public func setup(dependency: ViewControllerAccess) -> UIView {")
        if (android.files.size == 1) {
            forFile(android.files.first(), styles, conversion)
        } else {
            var first = true
            android.files.map {
                val key = it.parentFile.name.substringAfter("layout-", "")
                when {
                    key.isEmpty() -> Triple(it, null, 0)
                    key == "land" -> Triple(it, "pickLayout { UIDevice.current.orientation.isLandscape }", 10000)
                    //layout-w960dp
                    key.startsWith('w') && key.endsWith("dp") -> {
                        val size = key.substring(1, key.length-2).toInt()
                        Triple(it, "pickLayout { UIScreen.main.bounds.width > $size }", size)
                    }
                    else -> Triple(it, "", -1)
                }
            }.sortedBy { -it.third }.forEach { (file, condition, priority) ->
                if(first) first = false
                else append(" else ")
                if(condition != null) {
                    append("if ($condition) ")
                }
                appendln("{")
                forFile(file, styles, conversion)
                append("}")
            }
            appendln()
        }
        appendln("    }")
        appendln("    ")
        android.bindings.values.forEach {
            val type = conversion.bindings[it.name] ?: converter.viewTypes[it.type]?.iosName
            if (it.optional) {
                appendln("public var ${it.name.safeSwiftIdentifier()}: ${type}? = nil")
            } else {
                appendln("public var ${"_".plus(it.name).safeSwiftIdentifier()}: ${type}!")
                appendln(
                    "public var ${it.name.safeSwiftIdentifier()}: ${type} { get { return ${
                        "_".plus(it.name).safeSwiftIdentifier()
                    } } set(value){ ${"_".plus(it.name).safeSwiftIdentifier()} = value } }"
                )
            }
        }
        android.delegateBindings.values.forEach {
            val type = conversion.delegateBindings[it.name]
            if (it.optional) {
                appendln("public var ${(it.name + "Delegate").safeSwiftIdentifier()}: ${type}? = nil")
            } else {
                appendln("public var ${"_".plus(it.name).plus("Delegate").safeSwiftIdentifier()}: ${type}!")
                appendln(
                    "public var ${(it.name + "Delegate").safeSwiftIdentifier()}: ${type} { get { return ${
                        "_".plus(
                            it.name
                        ).plus("Delegate").safeSwiftIdentifier()
                    } } set(value) { ${"_".plus(it.name).plus("Delegate").safeSwiftIdentifier()} = value } }"
                )
            }
        }
        android.sublayouts.values.forEach {
            val type = conversion.sublayouts[it.name] ?: it.layoutXmlClass
            if (it.optional) {
                if (conversion.sublayouts.containsKey(it.name)) {
                    appendln("public let ${it.name.safeSwiftIdentifier()}: ${type}? = ${type}()")
                } else {
                    appendln("public let ${it.name.safeSwiftIdentifier()}: ${type}? = nil")
                }
            } else {
                appendln("public let ${it.name.safeSwiftIdentifier()}: ${type} = ${type}()")
            }
        }
        appendln("    ")
        appendln("}")
    }

    return appendable.toString()
}

private fun Appendable.forFile(file: File, styles: Styles, conversion: OngoingLayoutConversion) {
    val root = XmlNode.read(file, styles) { name ->
        file.parentFile.parentFile.resolve("layout").resolve(name).takeIf { it.exists() }
    }
    append("        let view = ")
    conversion.construct(root)
    appendln()
    conversion.writeSetup(root)
    appendln("        xmlRoot = view")
    appendln("        for test in _layoutTests { dependency.pickLayout(view: view, passOrFail: test) }")
    appendln("        return view")
}