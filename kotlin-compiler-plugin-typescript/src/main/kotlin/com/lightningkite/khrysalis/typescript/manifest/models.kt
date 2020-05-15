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
        val f = File(file.virtualFilePath.removePrefix(commonPath).removeSuffix(".kt").plus(".ts"))
        fun add(decl: KtDeclaration, prepend: String = "", addThis: Boolean = true){
            if(decl.isPrivate()) return
            val name = decl.name ?: return
            if(addThis) {
                manifest["$pkg.$prepend$name"] = f
            }
            if(decl is KtClassOrObject){
                for(sub in decl.declarations){
                    if(sub is KtClassOrObject) add(sub, "$prepend$name.", sub.resolvedClass?.tsTopLevelMessedUp == true)
                }
            }
        }
        for (decl in file.declarations) { add(decl) }
    }
    return manifest
}