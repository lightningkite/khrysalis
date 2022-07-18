package com.lightningkite.khrysalis.swift

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation
import org.jetbrains.kotlin.resolve.calls.model.VariableAsFunctionResolvedCall
import org.jetbrains.kotlin.resolve.calls.util.getImplicitReceiverValue
import com.lightningkite.khrysalis.analysis.*
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver

private val suppressReceiverAdditionLocal = ThreadLocal<Boolean>()
var suppressReceiverAddition: Boolean
    get() = suppressReceiverAdditionLocal.get() ?: false
    set(value) {
        suppressReceiverAdditionLocal.set(value)
    }
fun SwiftTranslator.registerReceiver() {

    //Prepend 'this'
    handle<KtNameReferenceExpression>(
        condition = {
            if((typedRule.parent as? KtQualifiedExpression)?.selectorExpression == typedRule) return@handle false
            val resolved = typedRule.resolvedCall ?: return@handle false
            when(resolved){
                is VariableAsFunctionResolvedCall -> resolved.variableCall.getImplicitReceiverValue() != null
                else -> resolved.getImplicitReceiverValue() != null
            } && !suppressReceiverAddition
        },
        priority = 99,
        action = {
            -typedRule.getTsReceiver()
            -"."
            doSuper()
        }
    )

    handle<KtNameReferenceExpression>(
        condition = {
            if((typedRule.parent as? KtQualifiedExpression)?.selectorExpression == typedRule) return@handle false
            val resolved = typedRule.resolvedCall ?: return@handle false
            val targetDescriptor =
                resolved.dispatchReceiver?.type?.constructor?.declarationDescriptor as? ClassDescriptor
                    ?: return@handle false
            return@handle resolved.getImplicitReceiverValue() != null
                    && targetDescriptor.isCompanionObject
                    && targetDescriptor != typedRule.containingClass()?.resolvedClass
                    && targetDescriptor != typedRule.parentOfType<KtObjectDeclaration>()?.resolvedClass
                    && !suppressReceiverAddition
        },
        priority = 100,
        action = {
            val resolved = typedRule.resolvedCall!!
            val targetDescriptor =
                resolved.dispatchReceiver?.type?.constructor?.declarationDescriptor as ClassDescriptor
            -targetDescriptor.swiftTopLevelName
            -".INSTANCE."
            -typedRule.getIdentifier()
        }
    )

    handle<KtThisExpression> {
        -typedRule.getTsReceiver()
    }
}