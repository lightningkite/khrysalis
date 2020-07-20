package com.lightningkite.khrysalis.swift

import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtPostfixExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.types.isNullable

fun SwiftTranslator.registerExpression() {
    handle<KtBinaryExpression>(
        condition = { typedRule.operationToken == KtTokens.ELVIS },
        priority = 1000,
        action = {
            -typedRule.left
            -" ?? "
            -typedRule.right
        }
    )
    handle<KtSafeQualifiedExpression> {
        val plainRt = when(val c = typedRule.receiverExpression.resolvedCall?.candidateDescriptor){
            is FunctionDescriptor -> c.returnType
            is PropertyDescriptor -> c.type
            else -> null
        }
        if(plainRt?.isNullable() == false) {
            -typedRule.receiverExpression
            insertNewlineBeforeAccess()
            -'.'
            -typedRule.selectorExpression
        } else {
            -typedRule.receiverExpression
            -"?"
            insertNewlineBeforeAccess()
            -"."
            -typedRule.selectorExpression
        }
    }
    handle<KtPostfixExpression>(
        condition = { typedRule.operationToken == KtTokens.EXCLEXCL },
        priority = 10000,
        action = {
            -typedRule.baseExpression
            -"!"
        }
    )
}
