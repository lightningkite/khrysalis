package com.lightningkite.khrysalis.analysis

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.resolve.calls.model.VariableAsFunctionResolvedCall
import org.jetbrains.kotlin.resolve.calls.resolvedCallUtil.getImplicitReceiverValue


fun PsiElement.capturesSelf(
    containingDeclaration: ClassDescriptor?,
    immediate: Boolean = true
): Boolean {
    val it = this
    if (it is KtLambdaExpression) {
        return it.allChildren.any { it.capturesSelf(containingDeclaration, false) }
    }
    if (it is KtExpression) {
        var hasThis = false
        val resolved: CallableDescriptor?
        if (it is KtThisExpression) {
            hasThis = true
            resolved = (it.parent as? KtExpression)?.resolvedCall?.candidateDescriptor
        } else {
            resolved = it.resolvedCall?.candidateDescriptor
            hasThis = when (val r = it.resolvedCall) {
                is VariableAsFunctionResolvedCall -> r.variableCall.getImplicitReceiverValue() != null
                else -> r?.getImplicitReceiverValue() != null
            }
        }
        if (hasThis) {
            if (resolved is PropertyDescriptor) {
                val safe =
                    immediate && containingDeclaration?.unsubstitutedPrimaryConstructor?.valueParameters?.any { it.name.asString() == resolved.name.asString() } == true
                return !safe
            } else {
                return true
            }
        }
    }
    return it.allChildren.any { it.capturesSelf(containingDeclaration, immediate) }
}