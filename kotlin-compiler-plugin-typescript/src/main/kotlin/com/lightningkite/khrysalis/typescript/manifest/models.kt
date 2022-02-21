package com.lightningkite.khrysalis.typescript.manifest

import com.lightningkite.khrysalis.generic.outputRelativePath
import com.lightningkite.khrysalis.typescript.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import java.io.File

val declaresPrefix = "//! Declares "
fun TypescriptTranslator.generateFqToFileMap(outputDirectory: File, files: Collection<KtFile>): HashMap<String, File> {
    val manifest = HashMap<String, File>()
    for (file in files) {
        val pkg = file.packageFqName.asString()
        val f = file.outputRelativePath(commonPackage, "ts")
        for (decl in file.declarations) {
            if(decl.isPrivate()) continue
            val name = decl.name ?: continue
            manifest["$pkg.$name"] = outputDirectory.resolve(f)
        }
    }
    return manifest
}