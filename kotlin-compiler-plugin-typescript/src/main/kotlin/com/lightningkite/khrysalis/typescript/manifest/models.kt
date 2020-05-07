package com.lightningkite.khrysalis.typescript.manifest

import com.lightningkite.khrysalis.typescript.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

fun TypescriptTranslator.generateFqToFileMap(files: Collection<KtFile>): HashMap<String, String> {
    val manifest = HashMap<String, String>()
    for (file in files) {
        val pkg = file.packageFqName.asString()
        val path = file.virtualFilePath.removePrefix(commonPath).removeSuffix(".kt")
        fun add(decl: KtDeclaration, prepend: String = "", addThis: Boolean = true){
            if(decl.isPrivate()) return
            val name = decl.name ?: return
            if(addThis) {
                manifest["$pkg.$prepend$name"] = path
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