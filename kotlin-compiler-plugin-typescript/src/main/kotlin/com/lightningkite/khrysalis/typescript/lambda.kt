package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.util.forEachBetween
import com.lightningkite.khrysalis.util.simpleFqName
import org.jetbrains.kotlin.builtins.functions.FunctionInvokeDescriptor
import org.jetbrains.kotlin.builtins.getReceiverTypeFromFunctionType
import org.jetbrains.kotlin.builtins.isFunctionType
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.FlexibleType
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.WrappedType
import org.jetbrains.kotlin.lexer.KtTokens

fun TypescriptTranslator.registerLambda() {
    handle<KtLambdaExpression>(
        condition = { (typedRule.parent as? KtBinaryExpression)?.operationToken == KtTokens.ELVIS },
        priority = 1,
        action = {
            -'('
            doSuper()
            -')'
        }
    )
    handle<KtFunctionLiteral>(
        condition = { typedRule.resolvedFunction?.extensionReceiverParameter != null },
        priority = 100,
        action = {
            val resolved = typedRule.resolvedFunction!!
            withReceiverScope(resolved, "this_") { name ->
                -typedRule.typeParameterList
                -'('
                -name
                typedRule.valueParameters.takeUnless { it.isEmpty() }?.forEach {
                    -", "
                    -it
                } ?: run {
                    if (resolved.valueParameters.size == 1) {
                        -", it"
                    }
                }
                -") => "
                when (typedRule.bodyExpression?.statements?.size) {
                    null, 0 -> -"{}"
                    1 -> {
                        val s = typedRule.bodyExpression!!.statements.first()
                        if(s!!.actuallyCouldBeExpression){
                            -s
                        } else {
                            -"{\n"
                            -s
                            -"\n}"
                        }
                    }
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
            if (resolved?.valueParameters?.size == 1) {
                -"(it)"
            } else {
                -"()"
            }
        }
        -" => "
        when (typedRule.bodyExpression?.statements?.size) {
            null, 0 -> -"{}"
            1 -> {
                val s = typedRule.bodyExpression!!.statements.first()
                if(s!!.actuallyCouldBeExpression){
                    -s
                } else {
                    -"{\n"
                    -s
                    -"\n}"
                }
            }
            else -> {
                -"{\n"
                -typedRule.bodyExpression
                -"\n}"
            }
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
