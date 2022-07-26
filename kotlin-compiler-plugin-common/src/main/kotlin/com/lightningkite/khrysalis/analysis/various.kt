package com.lightningkite.khrysalis.analysis

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifier

val KtClass.canBeExtended: Boolean get() = when {
    this.hasModifier(KtTokens.ABSTRACT_KEYWORD)
            || this.hasModifier(KtTokens.SEALED_KEYWORD)
            || this.hasModifier(KtTokens.OPEN_KEYWORD) -> true
    else -> false
}
val KtClass.mustBeExtended: Boolean get() = when {
    this.hasModifier(KtTokens.ABSTRACT_KEYWORD)
            || this.hasModifier(KtTokens.SEALED_KEYWORD) -> true
    else -> false
}

val KtNamedFunction.canBeExtended: Boolean get() = when {
    hasModifier(KtTokens.OVERRIDE_KEYWORD) && containingClassOrObject?.let { it as? KtClass } ?.canBeExtended == true
            || this.hasModifier(KtTokens.ABSTRACT_KEYWORD)
            || this.hasModifier(KtTokens.SEALED_KEYWORD)
            || this.hasModifier(KtTokens.OPEN_KEYWORD) -> true
    else -> false
}
val KtNamedFunction.mustBeExtended: Boolean get() = when {
    containingClassOrObject?.let { it as? KtClass } ?.isInterface() == true ||
    this.hasModifier(KtTokens.ABSTRACT_KEYWORD) -> true
    else -> false
}

val KtProperty.canBeExtended: Boolean get() = when {
    hasModifier(KtTokens.OVERRIDE_KEYWORD) && containingClassOrObject?.let { it as? KtClass } ?.canBeExtended == true
            || this.hasModifier(KtTokens.ABSTRACT_KEYWORD)
            || this.hasModifier(KtTokens.SEALED_KEYWORD)
            || this.hasModifier(KtTokens.OPEN_KEYWORD) -> true
    else -> false
}
val KtProperty.mustBeExtended: Boolean get() = when {
    containingClassOrObject?.let { it as? KtClass } ?.isInterface() == true ||
    this.hasModifier(KtTokens.ABSTRACT_KEYWORD) -> true
    else -> false
}