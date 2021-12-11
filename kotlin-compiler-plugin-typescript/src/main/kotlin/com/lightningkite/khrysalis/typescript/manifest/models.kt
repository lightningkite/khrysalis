package com.lightningkite.khrysalis.typescript.manifest

import com.lightningkite.khrysalis.typescript.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import java.io.File

val declaresPrefix = "//! Declares "
fun TypescriptTranslator.generateFqToFileMap(files: Collection<KtFile>, output: File): HashMap<String, File> {
    val manifest = HashMap<String, File>()
    for (file in files) {
        val pkg = file.packageFqName.asString()
        val f = output.resolve(file.virtualFilePath.removePrefix(commonPath).removeSuffix(".kt").plus(".ts"))
        for (decl in file.declarations) {
            if(decl.isPrivate()) continue
            val name = decl.name ?: continue
            manifest["$pkg.$name"] = f
        }
    }
    return manifest
}