package com.lightningkite.khrysalis.typescript

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.codegen.AccessorForCompanionObjectInstanceFieldDescriptor
import org.jetbrains.kotlin.contracts.parsing.isInvocationKindEnum
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.isFinalOrEnum
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
}
