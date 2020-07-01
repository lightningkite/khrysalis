package com.lightningkite.khrysalis.swift

import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren

fun SwiftTranslator.registerFile() {
    handle<KtFile> {
        typedRule.allChildren.dropWhile { it !is KtDeclaration }.forEach {
            -it
        }
    }
}
