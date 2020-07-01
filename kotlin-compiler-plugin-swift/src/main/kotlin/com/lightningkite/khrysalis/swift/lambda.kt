package com.lightningkite.khrysalis.swift

import org.jetbrains.kotlin.psi.*

fun SwiftTranslator.registerLambda() {
    handle<KtFunctionLiteral>(
        condition = { typedRule.resolvedFunction?.extensionReceiverParameter != null },
        priority = 100,
        action = {
            val resolved = typedRule.resolvedFunction!!
            withReceiverScope(resolved) { name ->
                -typedRule.typeParameterList
                -"{ ("
                -name
                typedRule.valueParameters.takeUnless { it.isEmpty() }?.forEach {
                    -", "
                    -it
                } ?: run {
                    if (resolved.valueParameters.size == 1) {
                        -", it"
                    }
                }
                -") in "
                when (typedRule.bodyExpression?.statements?.size) {
                    null, 0 -> {-' '}
                    1 -> {
                        -' '
                        val s = typedRule.bodyExpression!!.statements.first()
                        -s
                    }
                    else -> {
                        -"\n"
                        -typedRule.bodyExpression
                        -"\n"
                    }
                }
                -"}"
            }
        }
    )
    handle<KtFunctionLiteral> {
        val resolved = typedRule.resolvedFunction
        -"{ "
        typedRule.valueParameterList?.let {
            -'('
            -it
            -')'
        } ?: run {
            if (resolved?.valueParameters?.size == 1) {
                -"(it)"
            } else {
                -"()"
            }
        }
        -" in "
        when (typedRule.bodyExpression?.statements?.size) {
            null, 0 -> {-' '}
            1 -> {
                val s = typedRule.bodyExpression!!.statements.first()
                -s
                -' '
            }
            else -> {
                -"\n"
                -typedRule.bodyExpression
                -"\n"
            }
        }
        -"}"
    }

    handle<KtLabeledExpression>(
        condition = { typedRule.baseExpression is KtLambdaExpression },
        priority = 100
    ) {
        -typedRule.baseExpression
    }

    handle<KtReturnExpression> {
        -"return"
        typedRule.returnedExpression?.let {
            -" "
            -it
        }
    }
}
