package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.builtins.getReturnTypeFromFunctionType
import org.jetbrains.kotlin.builtins.getValueParameterTypesFromFunctionType
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
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
            val captures = ArrayList<String>()
            if (typedRule.resolvedFunction?.annotations?.let {
                    it.hasAnnotation(FqName("com.lightningkite.khrysalis.WeakSelf")) || it.hasAnnotation(FqName("com.lightningkite.khrysalis.weakSelf"))
                } == true) {
                captures.add("weak self")
            } else if (typedRule.resolvedFunction?.annotations?.let {
                    it.hasAnnotation(FqName("com.lightningkite.khrysalis.UnownedSelf")) || it.hasAnnotation(FqName("com.lightningkite.khrysalis.unownedSelf"))
                } == true) {
                captures.add("unowned self")
            }
            typedRule.resolvedFunction?.annotations?.findAnnotation(FqName("com.lightningkite.khrysalis.CaptureUnowned"))?.allValueArguments?.get(Name.identifier("keys"))?.value?.let { it as? Array<String> }?.let {
                captures.addAll(it.map { "unowned $it" })
            }
            typedRule.resolvedFunction?.annotations?.findAnnotation(FqName("com.lightningkite.khrysalis.CaptureWeak"))?.allValueArguments?.get(Name.identifier("keys"))?.value?.let { it as? Array<String> }?.let {
                captures.addAll(it.map { "weak $it" })
            }
            if(captures.isNotEmpty()){
                -'['
                captures.forEachBetween(
                    forItem = { -it },
                    between = { -", " }
                )
                -"] "
            }
            resolved.valueParameters.let {
                -'('
                if (rec != null) {
                    -rec
//                    -": "
//                    writingParameter = true
//                    -resolved.extensionReceiverParameter?.type
//                    writingParameter = false
                    if (it.isNotEmpty()) -", "
                }
                it.withIndex().forEachBetween(
                    forItem = { (index, it) ->
                        if (it.name.isSpecial) {
                            -'_'
                        } else {
                            -it.name.asString()
                        }
                        writingParameter = true
//                        -(typedRule.valueParameters.getOrNull(index)?.typeReference ?: betterParameterTypes?.getOrNull(index) ?: it.type)
                        (typedRule.valueParameters.getOrNull(index)?.typeReference)?.let {
                            -": "
                            -it
                        }
                        writingParameter = false
                    },
                    between = { -", " }
                )
                -')'
            }
            (
                    resolved.annotations.findAnnotation(FqName("com.lightningkite.khrysalis.SwiftReturnType"))
                        ?: resolved.annotations.findAnnotation(FqName("com.lightningkite.khrysalis.swiftReturnType"))
                    )?.allValueArguments?.entries?.first()?.value?.value?.let {
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
