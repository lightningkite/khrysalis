package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.util.AnalysisExtensions
import com.lightningkite.khrysalis.util.forEachBetween
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import com.lightningkite.khrysalis.util.simpleFqName
import org.jetbrains.kotlin.builtins.*
import org.jetbrains.kotlin.builtins.functions.FunctionInvokeDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.FlexibleType
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.WrappedType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlin.types.typeUtil.isNullableNothing

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

    handle<KtFunctionLiteral> {
        val resolved = typedRule.resolvedFunction!!
        val reet = typedRule.resolvedExpectedExpressionType
            ?: (typedRule.parent as? KtLambdaExpression)?.resolvedExpectedExpressionType
            ?: ((typedRule.parent as? KtLambdaExpression)?.parent as? KtParameter)?.resolvedValueParameter?.type
        val betterParameterTypes = try { reet?.getValueParameterTypesFromFunctionType()?.map { it.type } } catch(e: Exception) { null }
        val betterReturnType = try { reet?.getReturnTypeFromFunctionType() } catch(e: Exception) { null }
        fun write(rec: Any? = null) {
            resolved.valueParameters.let {
                -'('
                if (rec != null) {
                    -rec
                    -": "
                    -resolved.extensionReceiverParameter?.type
                    if (it.isNotEmpty()) -", "
                }
                it.withIndex().forEachBetween(
                    forItem = { (index, it) ->
                        if (it.name.isSpecial) {
                            -'_'
                            -index.toString()
                        } else {
                            -it.name.asString().safeJsIdentifier()
                        }
                        -": "
                        -(typedRule.valueParameters.getOrNull(index)?.typeReference ?: betterParameterTypes?.getOrNull(index) ?: it.type)
                    },
                    between = { -", " }
                )
                -')'
            }
            resolved.annotations.findAnnotation(FqName("com.lightningkite.butterfly.tsReturnType"))?.allValueArguments?.entries?.first()?.value?.value?.let {
                -": $it"
            } ?: (betterReturnType ?: resolved.returnType)?.let {
                -": "
                -it
            }
            -" => "
            when (typedRule.bodyExpression?.statements?.size) {
                null, 0 -> {
                    -"{}"
                }
                1 -> {
                    val s = typedRule.bodyExpression!!.statements.first()
                    if (s!!.actuallyCouldBeExpression && resolved.returnType?.fqNameWithoutTypeArgs !in AnalysisExtensions.dontReturnTypes && resolved.returnType?.isNullableNothing() != true) {
                        -s
                    } else {
                        -"{\n"
                        -typedRule.bodyExpression
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
