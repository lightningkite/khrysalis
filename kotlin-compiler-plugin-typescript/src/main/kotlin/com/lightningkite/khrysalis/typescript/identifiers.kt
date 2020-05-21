package com.lightningkite.khrysalis.typescript

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.codegen.AccessorForCompanionObjectInstanceFieldDescriptor
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.contracts.parsing.isInvocationKindEnum
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.isFinalOrEnum
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation
import org.jetbrains.kotlin.psi.synthetics.SyntheticClassOrObjectDescriptor
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor

fun TypescriptTranslator.registerIdentifiers(){
//    handle<PsiE> {
//        val rule = typedRule
//        -when(val text = rule.text){
//            "string", "number", "boolean", "any", "void" -> "a" + text.capitalize()
//            else -> text
//        }
//    }
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
            val resolved = typedRule.resolvedReferenceTarget ?: return@handle false
            resolved is ClassDescriptor && typedRule.resolvedUsedAsExpression == true && resolved.kind != ClassKind.ENUM_ENTRY
        },
        priority = 1011,
        action = {
            -typedRule.getIdentifier()
            -".INSTANCE"
        }
    )
    handle<KtNameReferenceExpression> {
        -typedRule.getIdentifier()
        typedRule.resolvedReferenceTarget?.let { out.addImport(it) }
    }

    handle<LeafPsiElement>(
        condition = { typedRule.elementType === KtTokens.IDENTIFIER },
        priority = 1,
        action = {
            -typedRule.text.safeJsIdentifier()
        }
    )
}

fun String.safeJsIdentifier(): String = when(this){
    "number",
    "string",
    "undefined",
    "abstract",
    "arguments",
    "await",
    "boolean",
    "break",
    "byte",
    "case",
    "catch",
    "char",
    "class",
    "const",
    "continue",
    "debugger",
    "default",
    "delete",
    "do",
    "double",
    "else",
    "enum*",
    "eval",
    "export",
    "extends",
    "false",
    "final",
    "finally",
    "float",
    "for",
    "function",
    "goto",
    "if",
    "implements",
    "import",
    "in",
    "instanceof",
    "int",
    "interface",
    "let",
    "long",
    "native",
    "new",
    "null",
    "package",
    "private",
    "protected",
    "public",
    "return",
    "short",
    "static",
    "super",
    "switch",
    "synchronized",
    "this",
    "throw",
    "throws",
    "transient",
    "true",
    "try",
    "typeof",
    "var",
    "void",
    "volatile",
    "while",
    "with",
    "yield" -> "_" + this
    else -> this
}
/*
 */