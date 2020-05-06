package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.typescript.replacements.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

fun TypescriptTranslator.registerFile() {
    handle<KtFile> {
        typedRule.allChildren.dropWhile { it !is KtDeclaration }.forEach {
            -it
        }
    }
}
