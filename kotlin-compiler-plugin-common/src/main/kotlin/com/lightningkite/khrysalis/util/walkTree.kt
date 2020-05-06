package com.lightningkite.khrysalis.util

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.psiUtil.allChildren

fun PsiElement.walkTopDown(): Sequence<PsiElement> {
    return sequenceOf(this) + allChildren.flatMap { it.walkTopDown() }
}
fun PsiElement.walkBottomUp(): Sequence<PsiElement> {
    return allChildren.flatMap { it.walkBottomUp() } + sequenceOf(this)
}