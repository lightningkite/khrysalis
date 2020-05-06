package com.lightningkite.khrysalis.typescript.manifest

import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import com.lightningkite.khrysalis.typescript.tsFunctionGetName
import com.lightningkite.khrysalis.typescript.tsFunctionSetName
import com.lightningkite.khrysalis.typescript.tsName
import com.lightningkite.khrysalis.util.AnalysisExtensions
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.isPublic
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.isPublishedApi
import java.io.File

fun generateFqToFileMap(files: Collection<KtFile>, commonPath: String): HashMap<String, String> {
    val manifest = HashMap<String, String>()
    for (file in files) {
        val pkg = file.packageFqName.asString()
        val path = file.virtualFilePath.removePrefix(commonPath).removeSuffix(".kt")
        for (decl in file.declarations) {
            val name = decl.name ?: continue
            manifest["$pkg.$name"] = path
        }
    }
    return manifest
}