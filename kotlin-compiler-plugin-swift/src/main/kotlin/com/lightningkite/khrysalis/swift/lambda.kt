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
//        val betterParameterTypes = reet?.getValueParameterTypesFromFunctionType()?.map { it.type }
        val betterReturnType = try { reet?.getReturnTypeFromFunctionType() } catch(e: Exception) { null }
        fun write(rec: Any? = null) {
            -"{ "
            val captures = ArrayList<String>()
            if (typedRule.resolvedFunction?.annotations?.let {
                    it.hasAnnotation(FqName("com.lightningkite.butterfly.WeakSelf")) || it.hasAnnotation(FqName("com.lightningkite.butterfly.weakSelf"))
                } == true) {
                captures.add("weak self")
            } else if (typedRule.resolvedFunction?.annotations?.let {
                    it.hasAnnotation(FqName("com.lightningkite.butterfly.UnownedSelf")) || it.hasAnnotation(FqName("com.lightningkite.butterfly.unownedSelf"))
                } == true) {
                captures.add("unowned self")
            }
            typedRule.resolvedFunction?.annotations?.findAnnotation(FqName("com.lightningkite.butterfly.CaptureUnowned"))?.allValueArguments?.get(Name.identifier("keys"))?.value?.let { it as? Array<String> }?.let {
                captures.addAll(it.map { "unowned $it" })
            }
            typedRule.resolvedFunction?.annotations?.findAnnotation(FqName("com.lightningkite.butterfly.CaptureWeak"))?.allValueArguments?.get(Name.identifier("keys"))?.value?.let { it as? Array<String> }?.let {
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
                    if (it.isNotEmpty()) -", "
                }
                it.withIndex().forEachBetween(
                    forItem = { (index, it) ->
                        if (it.name.isSpecial) {
                            -'_'
                        } else {
                            -it.name.asString()
                        }
                        writingParameter++
//                        -(typedRule.valueParameters.getOrNull(index)?.typeReference ?: betterParameterTypes?.getOrNull(index) ?: it.type)
                        (typedRule.valueParameters.getOrNull(index)?.typeReference)?.let {
                            if(it.resolvedType?.let { replacements.getType(it)?.protocol } != true) {
                                -": "
                                -it
                            }
                        }
                        writingParameter--
                    },
                    between = { -", " }
                )
                -')'
            }
            (
                    resolved.annotations.findAnnotation(FqName("com.lightningkite.butterfly.SwiftReturnType"))
                        ?: resolved.annotations.findAnnotation(FqName("com.lightningkite.butterfly.swiftReturnType"))
                    )?.allValueArguments?.entries?.first()?.value?.value?.let {
                    -" -> $it"
                } ?: (betterReturnType ?: resolved.returnType)?.let {
                if(replacements.getType(it)?.protocol != true) {
                    -" -> "
                    -it
                }
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
