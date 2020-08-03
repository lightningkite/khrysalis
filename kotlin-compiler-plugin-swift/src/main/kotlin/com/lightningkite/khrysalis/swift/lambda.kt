package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.builtins.getReturnTypeFromFunctionType
import org.jetbrains.kotlin.builtins.getValueParameterTypesFromFunctionType
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.*

fun SwiftTranslator.registerLambda() {
    handle<KtFunctionLiteral> {
        val resolved = typedRule.resolvedFunction!!
        val reet = typedRule.resolvedExpectedExpressionType
            ?: (typedRule.parent as? KtLambdaExpression)?.resolvedExpectedExpressionType
            ?: ((typedRule.parent as? KtLambdaExpression)?.parent as? KtParameter)?.resolvedValueParameter?.type
        val betterParameterTypes = reet?.getValueParameterTypesFromFunctionType()?.map { it.type }
        val betterReturnType = reet?.getReturnTypeFromFunctionType()
        fun write(rec: Any? = null) {
            -"{ "
            if (typedRule.resolvedFunction?.annotations?.any { it.fqName?.asString() == "com.lightningkite.khrysalis.WeakSelf" } == true) {
                -"[weak self] "
            } else if (typedRule.resolvedFunction?.annotations?.any { it.fqName?.asString() == "com.lightningkite.khrysalis.UnownedSelf" } == true) {
                -"[unowned self] "
            }
            resolved.valueParameters.let {
                -'('
                if (rec != null) {
                    -rec
                    -": "
                    partOfParameter = true
                    -resolved.extensionReceiverParameter?.type
                    partOfParameter = false
                    if (it.isNotEmpty()) -", "
                }
                it.withIndex().forEachBetween(
                    forItem = { (index, it) ->
                        if (it.name.isSpecial) {
                            -'_'
                        } else {
                            -it.name.asString()
                        }
                        -": "
                        partOfParameter = true
                        -(typedRule.valueParameters.getOrNull(index)?.typeReference ?: betterParameterTypes?.getOrNull(index) ?: it.type)
                        partOfParameter = false
                    },
                    between = { -", " }
                )
                -')'
            }
            resolved.annotations.findAnnotation(FqName("com.lightningkite.khrysalis.SwiftReturnType"))?.allValueArguments?.entries?.first()?.value?.value?.let {
                -" -> $it"
            } ?: (betterReturnType ?: resolved.returnType)?.let {
                -" -> "
                -it
            }
            -" in "
            when (typedRule.bodyExpression?.statements?.size) {
                null, 0 -> {
                    -' '
                }
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

        if (resolved.extensionReceiverParameter != null) {
            withReceiverScope(resolved) { name ->
                write(name)
            }
        } else {
            write(null)
        }
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
