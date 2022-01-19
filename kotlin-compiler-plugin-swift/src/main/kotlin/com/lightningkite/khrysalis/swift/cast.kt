package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.util.forEachBetween
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import com.lightningkite.khrysalis.util.satisfies
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import com.lightningkite.khrysalis.analysis.*

fun SwiftTranslator.registerCast() {
    handle<KtExpression>(
        condition = {
            replacements.getImplicitCast(
                typedRule.resolvedExpressionTypeInfo?.type ?: return@handle false,
                typedRule.resolvedExpectedExpressionType ?: return@handle false
            ) != null
        },
        priority = 2_000_000
    ) {
        val cast = replacements.getImplicitCast(
            typedRule.resolvedExpressionTypeInfo?.type!!,
            typedRule.resolvedExpectedExpressionType!!
        )!!
        emitTemplate(
            template = cast.template,
            receiver = { doSuper() }
        )
    }
}

