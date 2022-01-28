package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.builtins.getReturnTypeFromFunctionType
import org.jetbrains.kotlin.builtins.getValueParameterTypesFromFunctionType
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.constants.StringValue
import com.lightningkite.khrysalis.analysis.*

fun SwiftTranslator.registerLambda() {
    handle<KtFunctionLiteral> {
        val resolved = typedRule.resolvedFunction!!
        val reet = typedRule.resolvedExpectedExpressionType
            ?: (typedRule.parent as? KtLambdaExpression)?.resolvedExpectedExpressionType
            ?: ((typedRule.parent as? KtLambdaExpression)?.parent as? KtParameter)?.resolvedValueParameter?.type
//        val betterParameterTypes = reet?.getValueParameterTypesFromFunctionType()?.map { it.type }
        val annotations = (resolved.annotations + (typedRule
            .let { it.parent as? KtLambdaExpression }
            ?.let { it.parent as? KtAnnotatedExpression }
            ?.let { it.annotationEntries.map { it.resolvedAnnotation } }
            ?: listOf())).filterNotNull()
        val betterReturnType = try {
            reet?.getReturnTypeFromFunctionType()
        } catch (e: Exception) {
            null
        }

        fun write(rec: Any? = null) {
            -"{ "
            val captures = ArrayList<String>()
            if (annotations.any {
                    it.fqName?.asString() == "com.lightningkite.khrysalis.WeakSelf" || it.fqName?.asString() == "com.lightningkite.khrysalis.weakSelf"
                }) {
                captures.add("weak self")
            } else if (annotations.any {
                    it.fqName?.asString() == "com.lightningkite.khrysalis.UnownedSelf" || it.fqName?.asString() == "com.lightningkite.khrysalis.unownedSelf"
                }) {
                captures.add("unowned self")
            }
            annotations.find { it.fqName?.asString() == "com.lightningkite.khrysalis.CaptureUnowned" }
                ?.allValueArguments?.get(
                    Name.identifier("keys")
                )
                ?.value?.let { it as? ArrayList<StringValue> }?.forEach {
                    captures.add("unowned ${it.value}" )
                }
            annotations.find { it.fqName?.asString() == "com.lightningkite.khrysalis.CaptureWeak" }
                ?.allValueArguments?.get(
                    Name.identifier("keys")
                )
                ?.value?.let { it as? ArrayList<StringValue> }?.forEach {
                    captures.add("weak ${it.value}" )
                }
            if (captures.isNotEmpty()) {
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
                            -it.name.asString().safeSwiftIdentifier()
                        }
//                        -(typedRule.valueParameters.getOrNull(index)?.typeReference ?: betterParameterTypes?.getOrNull(index) ?: it.type)
                        (typedRule.valueParameters.getOrNull(index)?.typeReference)?.let {
                            if (it.resolvedType?.let { replacements.getType(it)?.protocol } != true) {
                                -": "
                                -it
                            }
                        }
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
                if (replacements.getType(it)?.protocol != true) {
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
