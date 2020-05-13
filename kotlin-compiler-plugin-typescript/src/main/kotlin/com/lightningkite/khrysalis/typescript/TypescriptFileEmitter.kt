package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import com.lightningkite.khrysalis.util.SmartTabWriter
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getImportableDescriptor
import java.io.BufferedWriter

class TypescriptFileEmitter(val translator: TypescriptTranslator, val file: KtFile) : Appendable {
    val stringBuilder = StringBuilder()
    val out = SmartTabWriter(stringBuilder)
    private val imports = HashMap<String, ImportInfo>()
    val importedFqs = HashSet<String>()
    var fileEndingActions = ArrayList<() -> Unit>()

    fun write(writer: BufferedWriter, file: KtFile) {
        writer.appendln("// Generated by Khrysalis TypeScript converter")
        writer.appendln("// File: ${file.virtualFilePath}")
        writer.appendln("// Package: ${file.packageFqName.asString()}")
        importedFqs.sorted().forEach {
            writer.appendln("// Imported FQ name: ${it}")
        }
        imports.values.groupBy { it.path }.forEach { (path, parts) ->
            writer.append("import { ")
            writer.append(parts.sortedBy { it.asName ?: it.identifier }.joinToString(", ") {
                it.asName?.let { name ->
                    it.identifier + " as " + name
                } ?: it.identifier
            })
            writer.append(" } from '")
            writer.append(path)
            writer.appendln("'")
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

    private data class ImportInfo(
        val path: String,
        val identifier: String,
        val asName: String? = null
    )

    //Map of FQ name to import info
    fun addImport(path: String, identifier: String, asName: String? = null) {
        val fqName = "$path->$identifier"
        if (imports.containsKey(fqName)) return
        imports[fqName] = ImportInfo(path, identifier, asName)
    }

    private fun addImportFromFq(fqName: String, name: String) {
        translator.kotlinFqNameToFile[fqName]?.let {
            if (!file.virtualFilePath.endsWith(it + ".kt")) {
                addImport(TemplatePart.Import(it, name))
            } else {
                importedFqs.add("$fqName SKIPPED due to same file")
            }
        }
    }

    fun addImport(decl: DeclarationDescriptor, overrideName: String? = null) {
        val useDecl = when(decl) {
            is ConstructorDescriptor -> decl.containingDeclaration
            else -> decl
        }
        val name = overrideName ?: useDecl.name.asString()
        val fq = useDecl.fqNameSafe.asString()
        val n = "$fq TS $name"
        if (importedFqs.contains(n))
            return
        importedFqs.add(n)
        addImportFromFq(fq, name)
    }

    fun addImport(part: TemplatePart.Import) {
        addImport(
            path = part.path,
            identifier = part.identifier,
            asName = part.asName
        )
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