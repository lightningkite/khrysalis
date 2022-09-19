package com.lightningkite.khrysalis.swift

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import com.lightningkite.khrysalis.generic.KotlinTranslator
import com.lightningkite.khrysalis.analysis.*
import com.lightningkite.khrysalis.replacements.Template
import com.lightningkite.khrysalis.replacements.TemplatePart
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlin.types.typeUtil.TypeNullability
import org.jetbrains.kotlin.types.typeUtil.nullability

enum class AccessMode(val resultAllowsOptionalOp: Boolean, val usesDot: Boolean = false) {
    PLAIN_DOT(resultAllowsOptionalOp = false, usesDot = true),
    QUEST_DOT(resultAllowsOptionalOp = false, usesDot = true),
    DIRECT_OPT(resultAllowsOptionalOp = true, usesDot = false),
    PAREN_OPT(resultAllowsOptionalOp = true, usesDot = false)
}

fun getRuleTemplate(swiftTranslator: SwiftTranslator, rule: KtQualifiedExpression): Template? = with(swiftTranslator) {
    return when (val sel = rule.selectorExpression) {
        is KtCallExpression -> replacements.getCall(sel.resolvedCall!!)?.template
        is KtNameReferenceExpression -> (sel.resolvedReferenceTarget as? PropertyDescriptor)?.let {
            replacements.getGet(it, rule.receiverExpression.resolvedExpressionTypeInfo?.type)
        }?.template
        else -> null
    }
}

fun getSelectorNullable(swiftTranslator: SwiftTranslator, rule: KtQualifiedExpression): Boolean =
    with(swiftTranslator) {
        val nullability = when (val sel = rule.selectorExpression) {
            is KtCallExpression -> sel.resolvedCall?.candidateDescriptor?.returnType?.nullability()
            is KtNameReferenceExpression -> (sel.resolvedReferenceTarget as? PropertyDescriptor)?.type?.nullability()
            else -> null
        }
        return when(nullability) {
            TypeNullability.NOT_NULL -> false
            TypeNullability.NULLABLE -> true
            TypeNullability.FLEXIBLE -> {
                when (val sel = rule.selectorExpression) {
                    is KtCallExpression -> sel.resolvedCall?.candidateDescriptor?.let { swiftTranslator.replacements.getCall(it) }?.resultIsNullable ?: false
                    is KtNameReferenceExpression -> (sel.resolvedReferenceTarget as? PropertyDescriptor)?.let { swiftTranslator.replacements.getGet(it) }?.resultIsNullable ?: false
                    else -> false
                }
            }
            null -> false
        }
    }

fun getDirectlyNullable(swiftTranslator: SwiftTranslator, rule: KtExpression): Boolean =
    with(swiftTranslator) {
        return when (rule) {
            is KtQualifiedExpression -> getSelectorNullable(swiftTranslator, rule)
            else -> rule.resolvedExpressionTypeInfo?.type?.isNullable() == true
        }
    }

fun getAccessMode(swiftTranslator: SwiftTranslator, rule: KtQualifiedExpression): AccessMode = with(swiftTranslator) {
    if(rule is KtDotQualifiedExpression) return AccessMode.PLAIN_DOT
    val ruleTemplate = getRuleTemplate(swiftTranslator, rule)
    val templateIsThisDot =
        ruleTemplate != null && ruleTemplate.parts.getOrNull(0) is TemplatePart.Receiver && ruleTemplate.parts.getOrNull(
            1
        ).let { it is TemplatePart.Text && it.string.startsWith('.') }
    val receiverAccessMode =
        (rule.receiverExpression as? KtQualifiedExpression)?.let { getAccessMode(swiftTranslator, it) }
    val receiverAllowsOptionalAction = receiverAccessMode?.resultAllowsOptionalOp ?: true

    return if (rule.selectorExpression?.resolvedCall?.candidateDescriptor?.let { it as? FunctionDescriptor}?.swiftNameOverridden == null && (templateIsThisDot || ruleTemplate == null)) {
        if (receiverAllowsOptionalAction || getDirectlyNullable(swiftTranslator, rule.receiverExpression)) {
            AccessMode.QUEST_DOT
        } else {
            AccessMode.PLAIN_DOT
        }
    } else {
        if (receiverAllowsOptionalAction) {
            AccessMode.DIRECT_OPT
        } else {
            AccessMode.PAREN_OPT
        }
    }
}

inline fun KotlinTranslator<SwiftFileEmitter>.ContextByType<*>.nullWrapAction(
    swiftTranslator: SwiftTranslator,
    rule: KtQualifiedExpression,
    action: (Any, AccessMode) -> Unit
) = with(swiftTranslator) {
    val mode = getAccessMode(swiftTranslator, rule)
    val rec: Any = if (mode.resultAllowsOptionalOp) {
        val rec = "temp${uniqueNumber.getAndIncrement()}"
        if (rule.actuallyCouldBeExpression) {
            if (mode == AccessMode.PAREN_OPT) -"("
            -rule.receiverExpression
            if (mode == AccessMode.PAREN_OPT) -")"
            if (rule.hasNewlineBeforeAccess) {
                -"\n"
            }
            if (getSelectorNullable(swiftTranslator, rule)) {
                -".flatMap { $rec in "
            } else {
                -".map { $rec in "
            }
        } else {
            -"if let $rec = ("
            -rule.receiverExpression
            -") {\n"
        }
        rec
    } else rule.receiverExpression
    action(rec, mode)
//    action(listOf(rec, "/*$mode from ${(rule.receiverExpression as? KtQualifiedExpression)?.let { getAccessMode(swiftTranslator, it) }}*/"), mode)
    if (mode.resultAllowsOptionalOp) {
        if (rule.actuallyCouldBeExpression) {
            -" "
        } else {
            -"\n"
        }
        -"}"
    }
}
fun <T : KtQualifiedExpression> KotlinTranslator<SwiftFileEmitter>.ContextByType<T>.insertNewlineBeforeAccess() {
    if (typedRule.hasNewlineBeforeAccess) {
        -"\n"
    }
}