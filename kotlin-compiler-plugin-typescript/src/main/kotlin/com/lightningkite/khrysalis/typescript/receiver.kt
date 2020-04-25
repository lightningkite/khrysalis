package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.line
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.synthetics.SyntheticClassOrObjectDescriptor
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver
import org.jetbrains.kotlin.resolve.scopes.receivers.ImplicitClassReceiver

fun TypescriptTranslator.registerReceiver() {

    //Prepend 'this'
    handle<KtNameReferenceExpression>(
        condition = {
            if (typedRule.parent is KtDotQualifiedExpression) return@handle false
            if((typedRule.parent as? KtCallExpression)?.parent is KtDotQualifiedExpression) return@handle false
            val resolved = typedRule.resolvedCall
            return@handle resolved?.dispatchReceiver != null
        },
        priority = 99,
        action = {
            val resolved = typedRule.resolvedCall?.dispatchReceiver as? ImplicitClassReceiver
            if(resolved != null){
                collector?.report(CompilerMessageSeverity.INFO, "Receiver of ${typedRule.text} is ${resolved.type.getJetTypeFqName(false)} - ${resolved.classDescriptor.isCompanionObject}")
            }
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