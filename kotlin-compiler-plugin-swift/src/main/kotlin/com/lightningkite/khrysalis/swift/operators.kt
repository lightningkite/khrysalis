package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall

//class TestThing(){
//    operator fun dec
//}

data class ValueOperator(
    val left: Any,
    val right: Any,
    val functionDescriptor: FunctionDescriptor,
    val dispatchReceiver: Any? = null,
    val operationToken: IElementType,
    val resolvedCall: ResolvedCall<out CallableDescriptor>? = null
)

data class VirtualArrayGet(
    val arrayExpression: Any,
    val indexExpressions: List<Any>,
    val functionDescriptor: FunctionDescriptor,
    val dispatchReceiver: Any? = null,
    val resolvedCall: ResolvedCall<out CallableDescriptor>? = null
)


fun SwiftTranslator.registerOperators() {


    //Array access, get
    handle<KtArrayAccessExpression> {
        val f = typedRule.resolvedReferenceTarget as FunctionDescriptor
        -VirtualArrayGet(
            arrayExpression = typedRule.arrayExpression!!,
            indexExpressions = typedRule.indexExpressions,
            functionDescriptor = f,
            dispatchReceiver = typedRule.getTsReceiver(),
            resolvedCall = typedRule.resolvedCall
        )
    }
    handle<VirtualArrayGet> {
        val f = typedRule.functionDescriptor
        val doubleReceiver = f.dispatchReceiverParameter != null && f.extensionReceiverParameter != null
        if (doubleReceiver) {
            -typedRule.dispatchReceiver
            -"."
        } else if (f.dispatchReceiverParameter != null) {
            -typedRule.arrayExpression
            -"."
        }
        -(f.swiftNameOverridden ?: "get")
        -ArgumentsList(
            on = f,
            resolvedCall = typedRule.resolvedCall!!,
            prependArguments = if (doubleReceiver) listOf(typedRule.arrayExpression) else listOf()
        )
    }
    handle<VirtualArrayGet>(
        condition = {
            replacements.getCall(this@registerOperators, typedRule.resolvedCall ?: return@handle false) != null
        },
        priority = 10_001,
        action = {
            val resolvedCall = typedRule.resolvedCall!!
            val rule = replacements.getCall(this@registerOperators, resolvedCall)!!

            emitTemplate(
                requiresWrapping = true,
                template = rule.template,
                receiver = typedRule.arrayExpression,
                dispatchReceiver = typedRule.dispatchReceiver,
                allParameters = ArrayList<Any?>().apply {
                    typedRule.indexExpressions.forEachBetween(
                        forItem = { add(it) },
                        between = { add(", ") }
                    )
                },
                parameter = resolvedCall.template_parameter,
                typeParameter = resolvedCall.template_typeParameter,
                parameterByIndex = resolvedCall.template_parameterByIndex,
                typeParameterByIndex = resolvedCall.template_typeParameterByIndex
            )
        }
    )

    //Array access, set
    handle<KtBinaryExpression>(
        condition = {
            val arrayAccess = typedRule.left as? KtArrayAccessExpression ?: return@handle false
            arrayAccess.resolvedIndexedLvalueSet?.resultingDescriptor != null
                    && (typedRule.resolvedVariableReassignment == true || typedRule.operationToken == KtTokens.EQ)
        },
        priority = 12_000,
        action = {
            val arrayAccess = typedRule.left as KtArrayAccessExpression
            val setFunction = arrayAccess.resolvedIndexedLvalueSet!!.resultingDescriptor
            val needsClose = when {
                arrayAccess.arrayExpression?.isSimple() == true -> false
                arrayAccess.arrayExpression?.resolvedExpressionTypeInfo?.type?.requiresMutable() == true -> true
                else -> false
            }
            val handle: Any = when {
                arrayAccess.arrayExpression?.isSimple() == true -> {
                    arrayAccess.arrayExpression!!
                }
                arrayAccess.arrayExpression?.resolvedExpressionTypeInfo?.type?.requiresMutable() == true -> {
                    val tempArray = "array${uniqueNumber.getAndIncrement()}"
                    -"var $tempArray = "
                    -arrayAccess.arrayExpression
                    -"\n"
                    tempArray
                }
                else -> {
                    val tempArray = "array${uniqueNumber.getAndIncrement()}"
                    -"let $tempArray = "
                    -arrayAccess.arrayExpression
                    -"\n"
                    tempArray
                }
            }
            val tempIndexes = arrayAccess.indexExpressions.map {
                val tempName = "index${uniqueNumber.getAndIncrement()}"
                -"let $tempName = "
                -it
                -"\n"
                tempName
            }

            val right: Any = if (typedRule.operationToken == KtTokens.EQ) typedRule.right!! else ValueOperator(
                left = VirtualArrayGet(
                    arrayExpression = handle,
                    indexExpressions = tempIndexes,
                    functionDescriptor = arrayAccess.resolvedIndexedLvalueGet!!.resultingDescriptor,
                    dispatchReceiver = typedRule.getTsReceiver(),
                    resolvedCall = arrayAccess.resolvedIndexedLvalueGet
                ),
                right = typedRule.right!!,
                functionDescriptor = typedRule.operationReference.resolvedReferenceTarget as FunctionDescriptor,
                dispatchReceiver = typedRule.getTsReceiver(),
                operationToken = typedRule.operationToken,
                resolvedCall = typedRule.resolvedCall
            )
            val doubleReceiver =
                setFunction.dispatchReceiverParameter != null && setFunction.extensionReceiverParameter != null
            if (doubleReceiver) {
                -arrayAccess.getTsReceiver()
                -"."
            } else if (setFunction.dispatchReceiverParameter != null) {
                -handle
                -"."
            }
            -(setFunction.swiftNameOverridden ?: "set")
            -ArgumentsList(
                on = setFunction,
                resolvedCall = arrayAccess.resolvedIndexedLvalueSet!!,
                prependArguments = if (doubleReceiver) listOf(arrayAccess.arrayExpression!!) else listOf()
            )
            if(needsClose){
                -"\n"
                -arrayAccess.arrayExpression
                -" = "
                -handle
            }
        }
    )
    handle<KtBinaryExpression>(
        condition = {
            val arrayAccess = typedRule.left as? KtArrayAccessExpression ?: return@handle false
            val f = arrayAccess.resolvedIndexedLvalueSet ?: return@handle false
            (typedRule.resolvedVariableReassignment == true || typedRule.operationToken == KtTokens.EQ)
                    && replacements.getCall(this@registerOperators, f) != null
        },
        priority = 20_002,
        action = {
            val arrayAccess = typedRule.left as KtArrayAccessExpression
            val resolvedCall = arrayAccess.resolvedIndexedLvalueSet!!

            val reuseIdentifiers = typedRule.operationToken != KtTokens.EQ

            val needsClose = when {
                arrayAccess.arrayExpression?.isSimple() == true -> false
                arrayAccess.arrayExpression?.resolvedExpressionTypeInfo?.type?.requiresMutable() == true -> true
                else -> false
            }
            val handle: Any = when {
                arrayAccess.arrayExpression?.isSimple() == true -> {
                    arrayAccess.arrayExpression!!
                }
                arrayAccess.arrayExpression?.resolvedExpressionTypeInfo?.type?.requiresMutable() == true -> {
                    val tempArray = "array${uniqueNumber.getAndIncrement()}"
                    -"var $tempArray = "
                    -arrayAccess.arrayExpression
                    -"\n"
                    tempArray
                }
                else -> {
                    val tempArray = "array${uniqueNumber.getAndIncrement()}"
                    -"let $tempArray = "
                    -arrayAccess.arrayExpression
                    -"\n"
                    tempArray
                }
            }
            val tempIndexes = if(reuseIdentifiers) {
                arrayAccess.indexExpressions.map {
                    if(it.isSimple()) {
                        it
                    } else {
                        val t = "index${uniqueNumber.getAndIncrement()}"
                        -"let $t = "
                        -it
                        -"\n"
                        t
                    }
                }
            } else {
                arrayAccess.indexExpressions
            }

            val right: Any = if (reuseIdentifiers) ValueOperator(
                left = VirtualArrayGet(
                    arrayExpression = handle,
                    indexExpressions = tempIndexes,
                    functionDescriptor = arrayAccess.resolvedIndexedLvalueGet!!.candidateDescriptor,
                    dispatchReceiver = typedRule.getTsReceiver(),
                    resolvedCall = arrayAccess.resolvedIndexedLvalueGet
                ),
                right = typedRule.right!!,
                functionDescriptor = typedRule.resolvedCall!!.candidateDescriptor as FunctionDescriptor,
                dispatchReceiver = typedRule.getTsReceiver(),
                operationToken = typedRule.operationToken,
                resolvedCall = typedRule.resolvedCall
            ) else typedRule.right!!
            val rule = replacements.getCall(this@registerOperators, resolvedCall)!!
            emitTemplate(
                requiresWrapping = typedRule.actuallyCouldBeExpression,
                template = rule.template,
                receiver = handle,
                dispatchReceiver = arrayAccess.getTsReceiver(),
                value = right,
                allParameters = ArrayList<Any?>().apply {
                    tempIndexes.forEachBetween(
                        forItem = { add(it) },
                        between = { add(", ") }
                    )
                },
                parameter = resolvedCall.template_parameter,
                typeParameter = resolvedCall.template_typeParameter,
                parameterByIndex = resolvedCall.template_parameterByIndex,
                typeParameterByIndex = resolvedCall.template_typeParameterByIndex
            )
            if(needsClose){
                -"\n"
                -arrayAccess.arrayExpression
                -" = "
                -handle
            }
        }
    )

    //Operator
    handle<ValueOperator>(
        condition = {
            replacements.getCall(this@registerOperators, typedRule.resolvedCall ?: return@handle false) != null
        },
        priority = 10_000,
        action = {
            val resolvedCall = typedRule.resolvedCall!!
            val rule = replacements.getCall(this@registerOperators, resolvedCall)!!

            if (typedRule.operationToken == KtTokens.NOT_IN || typedRule.operationToken == KtTokens.EXCLEQ) {
                -"!("
            }

            val invertDirection =
                typedRule.operationToken == KtTokens.IN_KEYWORD || typedRule.operationToken == KtTokens.NOT_IN
            val left = if (invertDirection) typedRule.right else typedRule.left
            val right = if (invertDirection) typedRule.left else typedRule.right

            emitTemplate(
                requiresWrapping = true,
                template = rule.template,
                receiver = left,
                dispatchReceiver = typedRule.dispatchReceiver,
                operatorToken = when (val t = typedRule.operationToken) {
                    is KtSingleValueToken -> t.value
                    else -> null
                },
                allParameters = right,
                parameter = resolvedCall.template_parameter,
                typeParameter = resolvedCall.template_typeParameter,
                parameterByIndex = resolvedCall.template_parameterByIndex,
                typeParameterByIndex = resolvedCall.template_typeParameterByIndex
            )
            if (typedRule.operationToken == KtTokens.NOT_IN || typedRule.operationToken == KtTokens.EXCLEQ) {
                -")"
            }
        }
    )
    handle<ValueOperator> {
        if (typedRule.operationToken == KtTokens.NOT_IN || typedRule.operationToken == KtTokens.EXCLEQ) {
            -"!("
        }

        val invertDirection =
            typedRule.operationToken == KtTokens.IN_KEYWORD || typedRule.operationToken == KtTokens.NOT_IN
        val left = if (invertDirection) typedRule.right else typedRule.left
        val right = if (invertDirection) typedRule.left else typedRule.right
        val doubleReceiver =
            typedRule.functionDescriptor.dispatchReceiverParameter != null && typedRule.functionDescriptor.extensionReceiverParameter != null
        if (doubleReceiver) {
            -typedRule.dispatchReceiver
            -"."
        } else if (typedRule.dispatchReceiver != null) {
            -left
            -"."
        }
        -(typedRule.functionDescriptor.swiftNameOverridden ?: typedRule.functionDescriptor.name.asString())
        -ArgumentsList(
            on = typedRule.functionDescriptor,
            resolvedCall = typedRule.resolvedCall!!,
            prependArguments = if (typedRule.functionDescriptor.extensionReceiverParameter != null) listOf(left) else listOf()
        )
        if (typedRule.operationToken == KtTokens.NOT_IN || typedRule.operationToken == KtTokens.EXCLEQ) {
            -")"
        }
    }
    handle<KtBinaryExpression>(
        condition = {
            typedRule.operationReference.getReferencedNameElementType() != KtTokens.IDENTIFIER
                    && typedRule.operationReference.getReferencedNameElementType() != KtTokens.EQ
                    && typedRule.operationReference.resolvedReferenceTarget != null
        },
        priority = 10,
        action = {
            if (typedRule.resolvedVariableReassignment == true) {
                -typedRule.left
                -" = "
            }
            -ValueOperator(
                left = typedRule.left!!,
                right = typedRule.right!!,
                functionDescriptor = typedRule.operationReference.resolvedReferenceTarget as FunctionDescriptor,
                dispatchReceiver = typedRule.getTsReceiver(),
                operationToken = typedRule.operationToken,
                resolvedCall = typedRule.resolvedCall
            )
        })
    handle<KtBinaryExpression>(
        condition = {
            typedRule.operationReference.getReferencedNameElementType() ==KtTokens.EQEQ
        },
        priority = 100,
        action = {
            -typedRule.left
            -" == "
            -typedRule.right
        })
    handle<KtBinaryExpression>(
        condition = {
            typedRule.operationReference.getReferencedNameElementType() == KtTokens.EXCLEQ
        },
        priority = 100,
        action = {
            -typedRule.left
            -" != "
            -typedRule.right
        })

    handle<KtPrefixExpression>(
        condition = {
            replacements.getCall(this@registerOperators, typedRule.resolvedCall ?: return@handle false) != null
        },
        priority = 10_000,
        action = {
            val f = typedRule.resolvedCall!!
            val rule = replacements.getCall(this@registerOperators, f)!!

            emitTemplate(
                requiresWrapping = typedRule.actuallyCouldBeExpression,
                template = rule.template,
                receiver = typedRule.baseExpression,
                dispatchReceiver = if(f.candidateDescriptor.extensionReceiverParameter != null) typedRule.getTsReceiver() else typedRule.baseExpression
            )
        }
    )

    handle<KtPrefixExpression>(
        condition = { typedRule.operationReference.resolvedReferenceTarget != null },
        priority = 1_000,
        action = {
            val f = typedRule.operationReference.resolvedReferenceTarget as FunctionDescriptor
            val doubleReceiver = f.dispatchReceiverParameter != null && f.extensionReceiverParameter != null
            if (doubleReceiver) {
                -typedRule.getTsReceiver()
                -"."
            } else if (f.dispatchReceiverParameter != null) {
                -typedRule.baseExpression
                -"."
            }
            -(f.swiftNameOverridden ?: f.name.asString())
            -ArgumentsList(
                on = f,
                resolvedCall = typedRule.resolvedCall!!,
                prependArguments = if (f.extensionReceiverParameter != null) listOf(typedRule.baseExpression!!) else listOf()
            )
        }
    )

    handle<KtPostfixExpression>(
        condition = {
            replacements.getCall(this@registerOperators, typedRule.resolvedCall ?: return@handle false) != null
        },
        priority = 10_000,
        action = {
            val f = typedRule.resolvedCall!!
            val rule = replacements.getCall(this@registerOperators, f)!!

            emitTemplate(
                requiresWrapping = typedRule.actuallyCouldBeExpression,
                template = rule.template,
                receiver = typedRule.baseExpression,
                dispatchReceiver = if(f.candidateDescriptor.extensionReceiverParameter != null) typedRule.getTsReceiver() else typedRule.baseExpression
            )
        }
    )
}
