package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.FileEmitter
import com.lightningkite.khrysalis.replacements.Import
import com.lightningkite.khrysalis.replacements.TemplatePart
import com.lightningkite.khrysalis.typescript.replacements.TypescriptImport
import com.lightningkite.khrysalis.util.SmartTabWriter
import com.lightningkite.khrysalis.util.fqNamesToCheck
import com.lightningkite.khrysalis.util.simpleFqName
import com.lightningkite.khrysalis.util.simplerFqName
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.load.java.descriptors.JavaPropertyDescriptor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getImportableDescriptor
import org.jetbrains.kotlin.synthetic.SyntheticJavaPropertyDescriptor
import java.io.BufferedWriter
import java.io.File

class TypescriptFileEmitter(val translator: TypescriptTranslator, file: KtFile) : FileEmitter(file) {
    val stringBuilder = StringBuilder()
    val out = SmartTabWriter(stringBuilder)
    private val imports = HashMap<String, TypescriptImport>()
    val importedFqs = HashSet<String>()
    val missedImports = HashSet<String>()

    companion object {
        val overwriteWarning = "// Generated by Khrysalis TypeScript converter - this file will be overwritten."
    }

    fun write(writer: BufferedWriter, file: KtFile) {
        render(writer)
        writer.flush()
    }

    //Map of FQ name to import info
    fun addImport(path: String, identifier: String, asName: String? = null) {
        val fqName = "$path->$identifier"
        if (imports.containsKey(fqName)) return
        imports[fqName] = TypescriptImport(path, identifier, asName)
    }

    private fun addImportFromFq(fqName: String, name: String): Boolean {
        val newImport = translator.declarations.importLine(
            currentRelativeFile = File(file.virtualFilePath.removePrefix(translator.commonPath).removeSuffix(".kt").plus(".ts")),
            fqName = fqName,
            name = name
        )
        if(newImport != null) {
            addImport(newImport)
            return true
        }
        return false
    }

    fun addImport(decl: DeclarationDescriptor, overrideName: String? = null) {
        val useDecl = when (decl) {
            is ConstructorDescriptor -> decl.containingDeclaration
            is ClassDescriptor -> if(decl.isCompanionObject) decl.containingDeclaration else decl
            else -> decl
        }
        val name = overrideName ?: useDecl.name.asString().safeJsIdentifier()
        val n = "${useDecl.simpleFqName} TS $name"
        if (importedFqs.contains(n))
            return
        importedFqs.add(n)
        useDecl.fqNamesToCheck.firstOrNull {
            addImportFromFq(it, name)
        } ?: missedImports.add(n)
    }

    override fun addImport(import: Import) {
        if(import is TypescriptImport) {
            addImport(
                path = import.path,
                identifier = import.identifier,
                asName = import.asName
            )
        } else throw IllegalArgumentException("TypescriptImport expected, got $import")
    }

    override fun renderImports(to: Appendable) {
        val relPath = file.virtualFilePath.removePrefix(translator.commonPath)
        renderImports(translator.projectName, relPath, imports.values, to)
    }

    override fun sub(): FileEmitter = TypescriptFileEmitter(translator, file)

    fun addImports(parts: Iterable<TypescriptImport>) {
        for (p in parts) addImport(p)
    }
}