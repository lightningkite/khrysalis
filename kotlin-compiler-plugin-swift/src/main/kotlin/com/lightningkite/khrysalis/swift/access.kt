package com.lightningkite.khrysalis.swift

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import com.lightningkite.khrysalis.generic.PartialTranslatorByType
import com.lightningkite.khrysalis.analysis.*
import com.lightningkite.khrysalis.replacements.Template
import com.lightningkite.khrysalis.replacements.TemplatePart
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.types.isNullable

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
        return when (val sel = rule.selectorExpression) {
            is KtCallExpression -> sel.resolvedCall?.candidateDescriptor?.returnType?.isNullable() ?: true
            is KtNameReferenceExpression -> (sel.resolvedReferenceTarget as? PropertyDescriptor)?.type?.isNullable()
                ?: true
            else -> true
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

inline fun PartialTranslatorByType<SwiftFileEmitter, Unit, Any>.ContextByType<*>.nullWrapAction(
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
            if (hasNewlineBeforeAccess(rule)) {
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

fun hasNewlineBeforeAccess(typedRule: KtQualifiedExpression): Boolean {
    return typedRule.allChildren
        .find { it is LeafPsiElement && (it.elementType == KtTokens.DOT || it.elementType == KtTokens.SAFE_ACCESS) }
        ?.prevSibling
        ?.let { it as? PsiWhiteSpace }
        ?.textContains('\n') == true
}

fun <T : KtQualifiedExpression> PartialTranslatorByType<SwiftFileEmitter, Unit, Any>.ContextByType<T>.insertNewlineBeforeAccess() {
    if (hasNewlineBeforeAccess(typedRule)) {
        -"\n"
    }
}