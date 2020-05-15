package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.FlexibleType
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.WrappedType

fun TypescriptTranslator.registerLambda() {
    handle<KtFunctionLiteral>(
        condition = { typedRule.resolvedFunction?.extensionReceiverParameter != null },
        priority = 100,
        action = {
            val resolved = typedRule.resolvedFunction!!
            withReceiverScope(resolved.fqNameSafe.asString(), "this_"){ name ->
                -typedRule.typeParameterList
                -'('
                -name
                typedRule.valueParameters.takeUnless { it.isEmpty() }?.forEach {
                    -", "
                    -it
                } ?: run {
                    if(resolved.valueParameters.size == 1){
                        -", it"
                    }
                }
                -") => "
                when(typedRule.bodyExpression?.statements?.size){
                    null, 0 -> -"{}"
                    1 -> -typedRule.bodyExpression?.firstStatement
                    else -> {
                        -"{\n"
                        -typedRule.bodyExpression
                        -"\n}"
                    }
                }
            }
        }
    )
    handle<KtFunctionLiteral> {
        val resolved = typedRule.resolvedFunction
        -typedRule.typeParameterList
        typedRule.valueParameterList?.let {
            -'('
            -it
            -')'
        } ?: run {
            if(resolved?.valueParameters?.size == 1){
                -"(it)"
            } else {
                -"()"
            }
        }
        -" => "
        when(typedRule.bodyExpression?.statements?.size){
            null, 0 -> -"{}"
            1 -> -typedRule.bodyExpression?.firstStatement
            else -> {
                -"{\n"
                -typedRule.bodyExpression
                -"\n}"
            }
        }
    }

    handle<KtLabeledExpression>(
        condition = {typedRule.baseExpression is KtLambdaExpression},
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
