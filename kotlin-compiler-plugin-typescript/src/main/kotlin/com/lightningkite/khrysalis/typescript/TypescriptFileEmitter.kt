package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.analysis.resolvedDeclarationToDescriptor
import com.lightningkite.khrysalis.generic.FileEmitter
import com.lightningkite.khrysalis.generic.outputRelativePath
import com.lightningkite.khrysalis.replacements.Import
import com.lightningkite.khrysalis.typescript.replacements.TypescriptImport
import com.lightningkite.khrysalis.util.SmartTabWriter
import com.lightningkite.khrysalis.util.fqNamesToCheck
import com.lightningkite.khrysalis.util.simpleFqName
import com.lightningkite.khrysalis.util.walkTopDown
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.util.capitalizeDecapitalize.capitalizeAsciiOnly
import java.io.BufferedWriter
import java.util.concurrent.atomic.AtomicInteger

class TypescriptFileEmitter(val translator: TypescriptTranslator, file: KtFile) : FileEmitter(file) {
    val stringBuilder = StringBuilder()
    val out = SmartTabWriter(stringBuilder)
    private val imports = HashMap<String, TypescriptImport>()
    val missedImports = HashSet<String>()

    val uniqueNumber = AtomicInteger(0)

    val takenIdentifiers = HashSet<String>()

    init {
        // Identifier analysis
        // Local top-level names get top priority
        file.declarations.forEach { it.resolvedDeclarationToDescriptor?.name?.identifier?.let { takenIdentifiers.add(it) } }
        file.walkTopDown()
            .filterIsInstance<KtProperty>()
            .filter { it.isLocal }
            .forEach { it.name?.let { takenIdentifiers.add(it) } }
    }

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

    fun addImport(decl: DeclarationDescriptor, overrideName: String? = null): StringReplacement? {
        val useDecl = when (decl) {
            is ConstructorDescriptor -> decl.containingDeclaration
            is ClassDescriptor -> if (decl.isCompanionObject) decl.containingDeclaration else decl
            else -> decl
        }
        val name = overrideName ?: useDecl.name.asString().safeJsIdentifier()
        return useDecl.fqNamesToCheck.asSequence()
            .mapNotNull {
                translator.declarations.importLine(
                    from = file,
                    fqName = it,
                    name = name
                )
            }
            .firstOrNull()
            ?.let { addImport(it) }
    }

    fun addImportGetName(decl: DeclarationDescriptor, overrideName: String? = null): String {
        return addImport(decl, overrideName)?.to ?: overrideName ?: when (decl) {
            is ConstructorDescriptor -> decl.containingDeclaration.name.asString().safeJsIdentifier()
            is ClassDescriptor -> decl.name.asString().safeJsIdentifier()
            else -> decl.name.asString().safeJsIdentifier()
        }
    }

    override fun addImport(import: Import): StringReplacement? {
        if (import is TypescriptImport) {
            val desiredName = import.asName ?: import.identifier
            val fqName = "${import.path}->${import.identifier}"
            imports[fqName]?.let { existing ->
                if ((existing.asName ?: existing.identifier) == desiredName)
                    return null
                else
                    return StringReplacement(desiredName, existing.asName ?: existing.identifier)
            }
            if (takenIdentifiers.add(desiredName)) {
                addImport(
                    path = import.path,
                    identifier = import.identifier,
                    asName = desiredName
                )
                return null
            }
            var charsToTake = 1
            while (true) {
                val newName = import.path
                    .substringAfterLast('/')
                    .filter { it.isLetter() }
                    .let {
                        if(charsToTake < it.length)
                            it.substring(0, charsToTake)
                        else
                            it + ('A' + (charsToTake - it.length))
                    }
                    .plus(desiredName.capitalizeAsciiOnly())
                if (takenIdentifiers.add(newName)) {
                    addImport(
                        path = import.path,
                        identifier = import.identifier,
                        asName = newName
                    )
                    return StringReplacement(desiredName, newName)
                }
                charsToTake++
            }
        } else throw IllegalArgumentException("TypescriptImport expected, got $import")
    }

    override fun renderImports(to: Appendable) {
        val relPath = file.outputRelativePath(translator.commonPackage, "ts")
        renderImports(translator.projectName, relPath, imports.values, to)
    }

    override fun sub(): FileEmitter = TypescriptFileEmitter(translator, file)

    fun addImports(parts: Iterable<TypescriptImport>) {
        for (p in parts) addImport(p)
    }
}