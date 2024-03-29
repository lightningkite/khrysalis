package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.analysis.resolvedExpectedExpressionType
import com.lightningkite.khrysalis.analysis.resolvedReferenceTarget
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtension
import org.jetbrains.kotlin.types.KotlinType

fun SwiftTranslator.registerReflection() {
    fun KtExpression.expectedReceiver(): KotlinType? {
        return this.resolvedExpectedExpressionType?.arguments?.get(0)?.type
    }

    handle<KtCallableReferenceExpression>(
        condition = {
            (typedRule.callableReference.resolvedReferenceTarget as? FunctionDescriptor)?.let {
                replacements.getCall(it, typedRule.expectedReceiver()) != null
            } == true
        },
        priority = 10
    ) {
        val replacement = replacements.getCall(
            typedRule.callableReference.resolvedReferenceTarget as FunctionDescriptor,
            typedRule.expectedReceiver()
        )!!
        replacement.reflectiveName?.let {
            emitTemplate(template = it)
        } ?: run {
            -typedRule.receiverExpression?.let { KtUserTypeBasic(it) }
            -'.'
            replacement.template.toString().substringBefore("(").substringAfterLast('.')
        }
    }
    handle<KtCallableReferenceExpression>(
        condition = {
            (typedRule.callableReference.resolvedReferenceTarget as? PropertyDescriptor)?.let {
                replacements.getGet(it, typedRule.expectedReceiver()) != null
            } == true
        },
        priority = 10
    ) {
        val replacement = replacements.getGet(
            typedRule.callableReference.resolvedReferenceTarget as PropertyDescriptor,
            typedRule.expectedReceiver()
        )!!
        replacement.reflectiveName?.let {
            emitTemplate(template = it)
        } ?: run {
            -'('
            -'\\'
            emitTemplate(
                template = replacement.template,
                receiver = typedRule.receiverExpression?.let { KtUserTypeBasic(it) }
            )
            -')'
        }
    }
    handle<KtCallableReferenceExpression>(
        condition = {
            val p = typedRule.callableReference.resolvedReferenceTarget as? PropertyDescriptor ?: return@handle false
            !p.isExtension && (p.containingDeclaration as? ClassDescriptor)?.let {
                it.isData && it.annotations.any {
                    it.fqName?.asString()?.endsWith("DatabaseModel") == true
                }
            } == true
        },
        priority = 6
    ) {
        val target = typedRule.callableReference.resolvedReferenceTarget as PropertyDescriptor
        -target.dispatchReceiverParameter!!.type
        -'.'
        -typedRule.callableReference
        -"Prop"
    }
    handle<KtCallableReferenceExpression>(
        condition = {
            typedRule.callableReference.resolvedReferenceTarget is PropertyDescriptor
        },
        priority = 5
    ) {
        -'('
        -'\\'
        -typedRule.receiverExpression?.let { KtUserTypeBasic(it) }
        -'.'
        -typedRule.callableReference
        -')'
    }
    handle<KtCallableReferenceExpression> {
        typedRule.receiverExpression?.let { KtUserTypeBasic(it) }?.let {
            -it
            -'.'
        }
        -typedRule.callableReference
    }
}