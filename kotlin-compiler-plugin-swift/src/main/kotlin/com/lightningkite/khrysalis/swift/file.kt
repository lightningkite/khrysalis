package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.swift.replacements.TemplatePart
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren

fun SwiftTranslator.registerFile() {
    handle<KtFile> {
        typedRule.importDirectives.forEach {
            it.importedReference?.text?.let {
                this@registerFile.fqToImport[it]?.let {
                    out.addImport(TemplatePart.Import(it))
                }
            }
        }
        typedRule.allChildren.dropWhile { it !is KtDeclaration }.forEach {
            -it
        }
    }
}
