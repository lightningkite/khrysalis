package com.lightningkite.khrysalis.util

import com.lightningkite.khrysalis.analysis.resolvedCall
import com.lightningkite.khrysalis.analysis.resolvedReferenceTarget
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.util.*
import org.jetbrains.kotlin.resolve.*
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.*
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver
import org.jetbrains.kotlin.types.DeferredType
import org.jetbrains.kotlin.types.TypeUtils
import java.util.HashMap


data class ReceiverAssignment(val declaration: DeclarationDescriptor, val tsName: String)

val identifierMappings: HashMap<DeclarationDescriptor, String> = HashMap()
val receiverStack: ArrayList<ReceiverAssignment> = ArrayList()

fun KtExpression.getReceiverReplacement(): String? {
    val dr = this.resolvedCall?.dispatchReceiver ?: this.resolvedCall?.extensionReceiver ?: run {
        return null
    }
    val target = if (dr is ExtensionReceiver) {
        dr.declarationDescriptor
    } else {
        dr.type.constructor.declarationDescriptor
    }
    val entry = receiverStack.lastOrNull { it.declaration == target }
    return entry?.tsName
}

fun KtThisExpression.getReceiverReplacement(): String? {
    val target = this.instanceReference.resolvedReferenceTarget
    val entry = receiverStack.lastOrNull { it.declaration == target }
    return entry?.tsName
}

inline fun withName(ktName: DeclarationDescriptor, tsName: String, action: (String)->Unit){
    identifierMappings[ktName] = tsName
    action(tsName)
    identifierMappings.remove(ktName)
}

inline fun withReceiverScope(
    descriptor: DeclarationDescriptor,
    suggestedName: String = "this_",
    action: (newIdentifier: String) -> Unit
) {
    var currentNumber = 0
    var tsName = suggestedName
    while (receiverStack.any { it.tsName == tsName }) {
        currentNumber++
        tsName = suggestedName + currentNumber
    }

    val newItem = ReceiverAssignment(descriptor, tsName)
    receiverStack.add(newItem)
    action(tsName)
    receiverStack.remove(newItem)
}