package com.lightningkite.khrysalis.ios.layout2

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.android.layout.AndroidLayoutFile
import com.lightningkite.khrysalis.generic.SmartTabWriter
import com.lightningkite.khrysalis.log
import com.lightningkite.khrysalis.swift.replacements.Replacements
import com.lightningkite.khrysalis.swift.replacements.TemplatePart
import com.lightningkite.khrysalis.swift.safeSwiftIdentifier
import com.lightningkite.khrysalis.utils.camelCase
import java.io.File

fun convertLayoutsToSwiftXmlClasses(
    androidLayoutsSummaryFile: File,
    equivalentsFolders: Sequence<File>,
    outputFolder: File
) {
    log("Creating layout classes")
    val replacements = Replacements()
    equivalentsFolders
        .flatMap { it.walkTopDown() }
        .filter { it.name.endsWith(".swift.yml") || it.name.endsWith(".swift.yaml") }
        .forEach {
            try {
                replacements += it
            } catch(e:Exception){
                throw IllegalArgumentException("Could not parse $it", e)
            }
        }
    val layouts = jacksonObjectMapper().readValue<Map<String, AndroidLayoutFile>>(androidLayoutsSummaryFile)
    for((name, layout) in layouts){
        val outputFile = outputFolder.resolve(name.camelCase().plus("Xml.swift"))
        outputFile.bufferedWriter().use {
            val s = SmartTabWriter(it)
            layout.toSwift(
                replacements = replacements,
                out = s
            )
        }
    }
}

fun AndroidLayoutFile.toSwift(
    replacements: Replacements,
    out: Appendable
) {
    log("Creating ${name}Xml.swift...")
    fun String.toSwiftType(): String {
        return (replacements.types[this]?.firstOrNull()
            ?: replacements.types["android.widget.$this"]?.firstOrNull()
            ?: replacements.types["android.view.$this"]?.firstOrNull()
                )?.template?.parts?.joinToString("") { (it as? TemplatePart.Text)?.string ?: "" } ?: this.substringAfterLast('.')
    }
    fun String.getImport(): TemplatePart.Import? {
        return (replacements.types[this]?.firstOrNull()
            ?: replacements.types["android.widget.$this"]?.firstOrNull()
            ?: replacements.types["android.view.$this"]?.firstOrNull()
                )?.template?.find { it is TemplatePart.Import } as? TemplatePart.Import
    }

    val imports = listOf(
        bindings.mapNotNull {
            it.value.type.getImport()
        },
        delegateBindings.mapNotNull {
            it.value.type.getImport()
        }
    ).flatten()

    out.appendln("//")
    out.appendln("// ${name}Xml.swift")
    out.appendln("// Created by Khrysalis XML Swift")
    out.appendln("//")
    out.appendln("import UIKit")
    for(import in imports.distinct()){
        out.appendln("import ${import.module}")
    }
    out.appendln("public class ${name}Xml: NSObject {")
    out.appendln("public unowned var xmlRoot: UIView!")
    sublayouts.entries.forEach {
        out.appendln("public let ${it.key}: ${it.value} = ${it.value}()")
    }
    delegateBindings.values.forEach {
        if(it.optional){
            out.appendln("public unowned var ${it.name}Delegate: ${it.type.toSwiftType()}?")
        } else {
            out.appendln("public unowned var ${it.name}Delegate: ${it.type.toSwiftType()}!")
        }
    }
    bindings.values.forEach {
        if(it.optional){
            out.appendln("@IBOutlet public unowned var ${it.name.safeSwiftIdentifier()}: ${it.type.toSwiftType()}? = nil")
        } else {
            out.appendln("@IBOutlet public unowned var ${it.name.safeSwiftIdentifier()}: ${it.type.toSwiftType()}!")
        }
    }
    out.appendln("    public func setup(dependency: ViewControllerAccess) -> UIView {")
    out.appendln("        let bundle = Bundle(for: type(of: self))")
    out.appendln("        let nib = UINib(nibName: \"$name\", bundle: bundle)")
    out.appendln("        let view = nib.instantiate(withOwner: self, options: nil)[0] as! UIView")
    out.appendln("        self.xmlRoot = view")
    out.appendln("        return view")
    out.appendln("    }")
    out.appendln("    ")
    out.appendln("}")
}