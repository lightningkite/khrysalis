package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.util.fqNameWithTypeArgs
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation
import org.jetbrains.kotlin.resolve.calls.smartcasts.MultipleSmartCasts
import com.lightningkite.khrysalis.analysis.*

fun SwiftTranslator.registerIdentifiers(){
//    handle<PsiE> {
//        val rule = typedRule
//        -when(val text = rule.text){
//            "string", "number", "boolean", "any", "void" -> "a" + text.capitalize()
//            else -> text
//        }
//    }
    handle<KtNameReferenceExpression>(
        condition = { identifierScopes.containsKey(typedRule.text) && (typedRule.parent as? KtQualifiedExpression)?.let { it.selectorExpression == typedRule } != true },
        priority = 100_000,
        action = {
            -identifierScopes[typedRule.text]!!.last()
        }
    )
    handle<KtNameReferenceExpression>(
        condition = {
            replacements.getGet(typedRule.resolvedReferenceTarget ?: return@handle false) != null
                    || replacements.getGet(typedRule.resolvedShortReferenceToCompanionObject ?: return@handle false) != null
        },
        priority = 10_011,
        action = {
            val rule = typedRule.resolvedReferenceTarget?.let { replacements.getGet(it) }
                ?: typedRule.resolvedShortReferenceToCompanionObject?.let { replacements.getGet(it) }!!
            emitTemplate(rule.template)
        }
    )

    handle<LeafPsiElement>(
        condition = { typedRule.elementType === KtTokens.IDENTIFIER },
        priority = 1,
        action = {
            -typedRule.text.safeSwiftIdentifier()
        }
    )

    //smartcast
    handle<KtNameReferenceExpression>(
        condition = {
            typedRule.resolvedSmartcast != null && !isSmartcastIgnored(typedRule.resolvedReferenceTarget as? ValueDescriptor)
        },
        priority = 4000,
        action = {
            val before = (typedRule.resolvedReferenceTarget as? ValueDescriptor)?.type
            val now = typedRule.resolvedSmartcast!!.defaultType ?: (typedRule.resolvedSmartcast as? MultipleSmartCasts)?.map?.values?.firstOrNull()
            if(before?.fqNameWithTypeArgs == now?.fqNameWithTypeArgs && now?.isMarkedNullable == false){
                doSuper()
                -'!'
            } else if(now != null) {
                -'('
                doSuper()
                -" as! "
                -now
                -')'
            } else {
                doSuper()
            }
        }
    )
    handle<KtDotQualifiedExpression>(
        condition = {
            typedRule.resolvedSmartcast != null && typedRule.selectorExpression is KtNameReferenceExpression
        },
        priority = 4000,
        action = {
            val sel = typedRule.selectorExpression as KtNameReferenceExpression
            val before = (sel.resolvedReferenceTarget as? ValueDescriptor)?.type
            val now = typedRule.resolvedSmartcast!!.defaultType ?: (typedRule.resolvedSmartcast as? MultipleSmartCasts)?.map?.values?.firstOrNull()
            if(before?.fqNameWithTypeArgs == now?.fqNameWithTypeArgs && now?.isMarkedNullable == false){
                doSuper()
                -'!'
            } else {
                -'('
                doSuper()
                -" as! "
                -now
                -')'
            }
        }
    )
}

fun String.safeSwiftIdentifier(): String = when(this){
    "associatedtype",
    "class",
    "deinit",
    "enum",
    "extension",
    "fileprivate",
    "func",
    "import",
    "init",
    "inout",
    "internal",
    "let",
    "open",
    "operator",
    "private",
    "protocol",
    "public",
    "rethrows",
    "static",
    "struct",
    "subscript",
    "typealias",
    "var",
    "break",
    "case",
    "continue",
    "default",
    "defer",
    "do",
    "else",
    "fallthrough",
    "for",
    "guard",
    "if",
    "in",
    "repeat",
    "return",
    "switch",
    "where",
    "while",
    "as",
    "Any",
    "catch",
    "false",
    "is",
    "nil",
    "super",
    "self",
    "Self",
    "throw",
    "throws",
    "true",
    "try" -> "`$this`"
    "description" -> "myDescription"
    else -> this
}
/*
 */