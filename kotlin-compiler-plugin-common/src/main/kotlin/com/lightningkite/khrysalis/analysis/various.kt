package com.lightningkite.khrysalis.analysis

import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifier
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

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

val KtProperty.isLazy: Boolean get() = ((delegateExpression as? KtCallExpression)?.calleeExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget?.fqNameOrNull()
    ?.asString() == "kotlin.lazy"
val KtProperty.isWeak: Boolean get() = ((delegateExpression as? KtCallExpression)?.calleeExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget?.fqNameOrNull()
    ?.asString() == "com.lightningkite.khrysalis.weak"


val KtQualifiedExpression.hasNewlineBeforeAccess: Boolean get() {
    return this.allChildren
        .find { it is LeafPsiElement && (it.elementType == KtTokens.DOT || it.elementType == KtTokens.SAFE_ACCESS) }
        ?.prevSibling
        ?.let { it as? PsiWhiteSpace }
        ?.textContains('\n') == true
}

fun KtQualifiedExpression.newlineify(rec: Any): Any {
    return if (hasNewlineBeforeAccess) listOf(
        rec,
        "\n"
    ) else rec
}