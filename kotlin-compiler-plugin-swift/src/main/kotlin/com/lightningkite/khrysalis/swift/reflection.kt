package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.analysis.resolvedReferenceTarget
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

fun SwiftTranslator.registerReflection() {
    handle<KtCallableReferenceExpression>(
        condition = {
            (typedRule.callableReference.resolvedReferenceTarget as? FunctionDescriptor)?.let {
                replacements.getCall(it) != null
            } == true
        },
        priority = 10
    ) {
        val replacement = replacements.getCall(typedRule.callableReference.resolvedReferenceTarget as FunctionDescriptor)!!
        replacement.reflectiveName?.let {
            -it
        } ?: run {
            (typedRule.receiverExpression as? KtSimpleNameExpression)?.let { -KtUserTypeBasic(it) }
            -'.'
            replacement.template.toString().substringBefore("(").substringAfterLast('.')
        }
    }
    handle<KtCallableReferenceExpression>(
        condition = {
            (typedRule.callableReference.resolvedReferenceTarget as? PropertyDescriptor)?.let {
                replacements.getGet(it) != null
            } == true
        },
        priority = 10
    ) {
        val replacement = replacements.getGet(typedRule.callableReference.resolvedReferenceTarget as PropertyDescriptor)!!
        replacement.reflectiveName?.let {
            -it
        } ?: run {
            -'\\'
            emitTemplate(
                template = replacement.template,
                receiver = (typedRule.receiverExpression as? KtSimpleNameExpression)?.let { KtUserTypeBasic(it) }
            )
        }
    }
    handle<KtCallableReferenceExpression>(
        condition = {
            typedRule.callableReference.resolvedReferenceTarget is PropertyDescriptor
        },
        priority = 10
    ) {
        -'\\'
//        (typedRule.receiverExpression as? KtSimpleNameExpression)?.let { -KtUserTypeBasic(it) }
        -'.'
        -typedRule.callableReference
    }
    handle<KtCallableReferenceExpression> {
        (typedRule.receiverExpression as? KtSimpleNameExpression)?.let { -KtUserTypeBasic(it) }
        -'.'
        -typedRule.callableReference
    }
}