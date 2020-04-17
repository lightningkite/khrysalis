package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.line
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.synthetics.SyntheticClassOrObjectDescriptor
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver

fun TypescriptTranslator.registerReceiver() {

    //Prepend 'this'
    handle<KtNameReferenceExpression>(
        condition = {
            if (typedRule.parent is KtDotQualifiedExpression) return@handle false
            val resolved = typedRule.resolvedCall
            return@handle resolved?.dispatchReceiver != null
        },
        priority = 99,
        action = {
            -typedRule.getTsReceiver()
            -"."
            doSuper()
        }
    )

    handle<KtThisExpression> {
        val fq = typedRule.resolvedCall?.resultingDescriptor?.containingDeclaration?.fqNameSafe?.asString()
        val entry = receiverStack.lastOrNull { it.fqName == fq }
        -(entry?.tsName ?: "this")
    }
}