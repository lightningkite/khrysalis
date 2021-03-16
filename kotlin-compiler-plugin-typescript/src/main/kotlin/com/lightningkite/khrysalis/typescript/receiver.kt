package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.PartialTranslatorByType
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
import org.jetbrains.kotlin.resolve.calls.resolvedCallUtil.getImplicitReceiverValue
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


inline fun PartialTranslatorByType<TypescriptFileEmitter, Unit, Any>.ContextByType<*>.nullWrapAction(
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
    if(isExpression){
        -"(()"
        if(type != null){
            -": "
            -type
        }
        -" => {\n"
    }
    val r = receiver
    val tempName = if (r is String || (r as? KtExpression)?.isSimple() == true) {
        -"if("
        -r
        -" !== null) {\n"
        r
    } else {
        val n = "temp${uniqueNumber.getAndIncrement()}"
        -"const $n = "
        -receiver
        -";\nif($n !== null) {\n"
        n
    }
    if(isExpression){
        -"return "
    }
    action(tempName)
    -"\n}"
    if(isExpression){
        -" else { return null }\n})()"
    }
}