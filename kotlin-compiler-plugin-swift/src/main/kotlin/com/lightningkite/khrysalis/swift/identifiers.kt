package com.lightningkite.khrysalis.swift

import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*

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
            val resolved = typedRule.resolvedReferenceTarget ?: return@handle false
            resolved is ClassDescriptor && resolved.isCompanionObject && typedRule.text == "Companion"
        },
        priority = 1013,
        action = {
            -typedRule.resolvedReferenceTarget!!.containingDeclaration?.name?.identifier
            -".Companion.INSTANCE"
        }
    )
    handle<KtNameReferenceExpression>(
        condition = {
            val resolved = typedRule.resolvedReferenceTarget ?: return@handle false
            resolved is ClassDescriptor && resolved.isCompanionObject
        },
        priority = 1012,
        action = {
            -typedRule.getIdentifier()
            -".Companion.INSTANCE"
        }
    )
    handle<KtNameReferenceExpression>(
        condition = {
            //Use .INSTANCE in all cases EXCEPT pointing to another type

            // Condition: I am not a receiver pointing to another type
            val parent = typedRule.parent as? KtDotQualifiedExpression
            if(parent?.receiverExpression == typedRule) {
                val next = (parent.selectorExpression as? KtQualifiedExpression)?.selectorExpression as? KtNameReferenceExpression ?: parent.selectorExpression as? KtNameReferenceExpression
                if(next?.resolvedReferenceTarget is ClassDescriptor){
                    return@handle false
                }
            }
            // Condition: I refer to a type
            val resolved = typedRule.resolvedReferenceTarget ?: return@handle false
            resolved is ClassDescriptor
                    && typedRule.resolvedUsedAsExpression == true
                    && resolved.kind != ClassKind.ENUM_ENTRY

        },
        priority = 1011,
        action = {
            -typedRule.getIdentifier()
            -".INSTANCE"
        }
    )
    handle<KtNameReferenceExpression>(
        condition = {
            typedRule.resolvedUsedAsExpression == true
                    && replacements.getGet(typedRule.resolvedReferenceTarget ?: return@handle false) != null
                    || replacements.getGet(typedRule.resolvedShortReferenceToCompanionObject ?: return@handle false) != null
        },
        priority = 10_011,
        action = {
            val rule = typedRule.resolvedReferenceTarget?.let { replacements.getGet(it) }
                ?: typedRule.resolvedShortReferenceToCompanionObject?.let { replacements.getGet(it) }!!
            emitTemplate(rule.template)
        }
    )
    //Naked local type references
    handle<KtNameReferenceExpression>(
        condition = {
            val untypedTarget = typedRule.resolvedReferenceTarget
            val target = untypedTarget as? ClassDescriptor ?: (untypedTarget as? ConstructorDescriptor)?.constructedClass ?: return@handle false
            if((typedRule.parent as? KtQualifiedExpression)?.selectorExpression == this) return@handle false
            val context = (typedRule.parentOfType<KtClassBody>()?.parent as? KtClassOrObject)?.resolvedClass ?: return@handle false
            target.containingDeclaration == context
        },
        priority = 10,
        action = {
            -(typedRule.parentOfType<KtClassBody>()?.parent as? KtClassOrObject)?.nameIdentifier
            -'.'
            -typedRule.getIdentifier()
        }
    )

    handle<LeafPsiElement>(
        condition = { typedRule.elementType === KtTokens.IDENTIFIER },
        priority = 1,
        action = {
            -typedRule.text.safeSwiftIdentifier()
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
    else -> this
}
/*
 */