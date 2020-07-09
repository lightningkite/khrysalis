package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.swift.replacements.TemplatePart
import com.lightningkite.khrysalis.util.SmartTabWriter
import com.lightningkite.khrysalis.util.simpleFqName
import com.lightningkite.khrysalis.util.simplerFqName
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.psi.KtFile
import java.io.BufferedWriter
import java.io.File

class SwiftFileEmitter(val translator: SwiftTranslator, val file: KtFile) : Appendable {
    val stringBuilder = StringBuilder()
    val out = SmartTabWriter(stringBuilder)
    private val imports = HashSet<TemplatePart.Import>()
    init {
        imports.add(TemplatePart.Import("Foundation"))
    }
    val importedFqs = HashSet<String>()
    var fileEndingActions = ArrayList<() -> Unit>()

    companion object {
        val overwriteWarning = "// Generated by Khrysalis Swift converter - this file will be overwritten."
    }

    fun write(writer: BufferedWriter, file: KtFile) {
        writer.appendln(overwriteWarning)
        val relPath = file.virtualFilePath.removePrefix(translator.commonPath)
        writer.appendln("// File: $relPath")
        writer.appendln("// Package: ${file.packageFqName.asString()}")
        for(imp in imports){
            writer.appendln("import ${imp.module}")
        }
        writer.appendln()

        out.flush()
        writer.appendln(stringBuilder)

        while (fileEndingActions.isNotEmpty()) {
            stringBuilder.clear()
            stringBuilder.appendln()
            val copy = fileEndingActions
            fileEndingActions = ArrayList()
            copy.forEach { it() }
            out.flush()
            writer.appendln(stringBuilder)
        }
    }

    fun addImport(part: TemplatePart.Import) {
        imports.add(part)
    }

    fun addImports(parts: Iterable<TemplatePart.Import>) {
        for (p in parts) addImport(p)
    }

    override fun append(p0: CharSequence): Appendable {
        out.append(p0)
        return this
    }

    override fun append(p0: CharSequence, p1: Int, p2: Int): Appendable {
        out.append(p0, p1, p2)
        return this
    }

    override fun append(p0: Char): Appendable {
        out.append(p0)
        return this
    }
}