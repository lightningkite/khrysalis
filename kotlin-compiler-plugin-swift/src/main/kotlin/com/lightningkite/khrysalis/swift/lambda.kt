package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.*

fun SwiftTranslator.registerLambda() {
    handle<KtFunctionLiteral>(
        condition = { typedRule.resolvedFunction?.extensionReceiverParameter != null },
        priority = 100,
        action = {
            val resolved = typedRule.resolvedFunction!!
            withReceiverScope(resolved) { name ->
                partOfParameter = true
                -"{ "
                if(typedRule.resolvedFunction?.annotations?.any { it.fqName?.asString() == "com.lightningkite.khrysalis.weakSelf" } == true) {
                    -"[weak self] "
                } else if(typedRule.resolvedFunction?.annotations?.any { it.fqName?.asString() == "com.lightningkite.khrysalis.unownedSelf" } == true) {
                    -"[unowned self] "
                }
                -"("
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
                partOfParameter = false
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
        if(typedRule.resolvedFunction?.annotations?.any { it.fqName?.asString() == "com.lightningkite.khrysalis.weakSelf" } == true) {
            -"[weak self] "
        } else if(typedRule.resolvedFunction?.annotations?.any { it.fqName?.asString() == "com.lightningkite.khrysalis.unownedSelf" } == true) {
            -"[unowned self] "
        }
        resolved?.valueParameters?.let {
            -'('
            it.withIndex().forEachBetween(
                forItem = { (index, it) ->
                    if(it.name.isSpecial){
                        -'_'
                    } else {
                        -it.name.asString()
                    }
                    -": "
                    partOfParameter = true
                    -(typedRule.valueParameters.getOrNull(index)?.typeReference ?: it.type)
                    partOfParameter = false
                },
                between = { -", " }
            )
            -')'
        } ?: typedRule.valueParameterList?.let {
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
        resolved?.annotations?.findAnnotation(FqName("com.lightningkite.khrysalis.swiftReturnType"))?.allValueArguments?.entries?.first()?.value?.value?.let {
            -" -> $it"
        } ?: resolved?.returnType?.let {
            - " -> "
            -it
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
