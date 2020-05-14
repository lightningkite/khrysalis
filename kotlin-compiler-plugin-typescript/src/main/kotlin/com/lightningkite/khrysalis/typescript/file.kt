package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.typescript.manifest.declaresPrefix
import com.lightningkite.khrysalis.typescript.replacements.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

fun TypescriptTranslator.registerFile() {
    handle<KtFile> {
//
//        typedRule.let { file ->
//            val pkg = file.packageFqName.asString()
//            fun add(decl: KtDeclaration, prepend: String = "", addThis: Boolean = true){
//                if(decl.isPrivate()) return
//                val name = decl.name ?: return
//                if(addThis) {
//                    -"$declaresPrefix$pkg.$prepend$name\n"
//                }
//                if(decl is KtClassOrObject){
//                    for(sub in decl.declarations){
//                        if(sub is KtClassOrObject) add(sub, "$prepend$name.", sub.resolvedClass?.tsTopLevelMessedUp == true)
//                    }
//                }
//            }
//            for (decl in file.declarations) { add(decl) }
//        }

        typedRule.allChildren.dropWhile { it !is KtDeclaration }.forEach {
            if(stubMode && it is KtDeclaration && it.isPrivate()) return@forEach
            -it
        }
    }
}
