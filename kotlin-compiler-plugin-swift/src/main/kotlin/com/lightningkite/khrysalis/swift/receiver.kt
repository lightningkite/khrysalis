package com.lightningkite.khrysalis.swift

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.resolve.calls.model.VariableAsFunctionResolvedCall
import org.jetbrains.kotlin.resolve.calls.resolvedCallUtil.getImplicitReceiverValue

fun SwiftTranslator.registerReceiver() {

    //Prepend 'this'
    handle<KtNameReferenceExpression>(
        condition = {
            val resolved = typedRule.resolvedCall ?: return@handle false
            when(resolved){
                is VariableAsFunctionResolvedCall -> resolved.variableCall.getImplicitReceiverValue() != null
                else -> resolved.getImplicitReceiverValue() != null
            }
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
            val resolved = typedRule.resolvedCall ?: return@handle false
            val targetDescriptor =
                resolved.dispatchReceiver?.type?.constructor?.declarationDescriptor as? ClassDescriptor
                    ?: return@handle false
            return@handle resolved.getImplicitReceiverValue() != null
                    && targetDescriptor.isCompanionObject
                    && targetDescriptor != typedRule.containingClass()?.resolvedClass
        },
        priority = 100,
        action = {
            -typedRule.containingClass()?.nameIdentifier
            -".Companion.INSTANCE."
            -typedRule.getIdentifier()
        }
    )

    handle<KtThisExpression> {
        -typedRule.getTsReceiver()
    }
}