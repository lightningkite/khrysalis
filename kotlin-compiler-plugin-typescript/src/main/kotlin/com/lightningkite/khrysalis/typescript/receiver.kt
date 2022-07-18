package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.KotlinTranslator
import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.util.simpleFqName
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.synthetics.SyntheticClassOrObjectDescriptor
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.VariableAsFunctionResolvedCall
import org.jetbrains.kotlin.resolve.calls.util.getImplicitReceiverValue
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver
import org.jetbrains.kotlin.resolve.scopes.receivers.ImplicitClassReceiver
import org.jetbrains.kotlin.types.typeUtil.isInterface
import com.lightningkite.khrysalis.analysis.*

fun TypescriptTranslator.registerReceiver() {

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
                    && targetDescriptor != typedRule.parentOfType<KtObjectDeclaration>()?.resolvedClass
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


inline fun KotlinTranslator<TypescriptFileEmitter>.ContextByType<*>.nullWrapAction(
    swiftTranslator: TypescriptTranslator,
    receiver: Any?,
    skip: Boolean,
    isExpression: Boolean,
    type: Any? = null,
    action: (Any?) -> Unit
) = with(swiftTranslator) {
    if(receiver == null || skip) {
        action(receiver)
        return
    }
    if(isExpression || (receiver !is String && (receiver as? KtExpression)?.isSimple() != true)){
        out.addImport("@lightningkite/khrysalis-runtime", "runOrNull")
        -"runOrNull("
        -receiver
        val tempName = "_"
        -", $tempName => "
        action(tempName)
        -")"
    } else {
        -"if("
        -receiver
        -" !== null) {\n"
        action(receiver)
        -"\n}"
    }
}