package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*

//class TestThing(){
//    operator fun dec
//}

data class ValueOperator(
    val left: Any,
    val right: Any,
    val functionDescriptor: FunctionDescriptor,
    val dispatchReceiver: Any? = null,
    val operationToken: IElementType
)

data class VirtualArrayGet(
    val arrayExpression: Any,
    val indexExpressions: List<Any>,
    val functionDescriptor: FunctionDescriptor,
    val dispatchReceiver: Any? = null
)

fun KtExpression.isSimple(): Boolean = when(this){
    is KtNameReferenceExpression,
    is KtConstantExpression,
    is KtThisExpression -> true
    else -> false
}

fun TypescriptTranslator.registerOperators() {


    //Array access, get
    handle<KtArrayAccessExpression> {
        val f = typedRule.resolvedReferenceTarget as FunctionDescriptor
        -VirtualArrayGet(
            arrayExpression = typedRule.arrayExpression!!,
            indexExpressions = typedRule.indexExpressions,
            functionDescriptor = f,
            dispatchReceiver = typedRule.getTsReceiver()
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
        -(f.tsName ?: "get")
        -ArgumentsList(
            on = f,
            prependArguments = if (doubleReceiver) listOf(typedRule.arrayExpression) else listOf(),
            orderedArguments = typedRule.indexExpressions.map { it to null },
            namedArguments = listOf(),
            lambdaArgument = null
        )
    }
    handle<VirtualArrayGet>(
        condition = {
            replacements.getCall(typedRule.functionDescriptor) != null
        },
        priority = 10_001,
        action = {
            val f = typedRule.functionDescriptor
            val rule = replacements.getCall(f)!!

            val allParametersByIndex = HashMap<Int, Any>()
            val allParametersByName = HashMap<String, Any>()
            typedRule.indexExpressions.forEachIndexed { index, valueArgument ->
                allParametersByIndex[index] = valueArgument
                f.valueParameters.getOrNull(index)?.name?.asString()?.let {
                    allParametersByName[it] = valueArgument
                }
            }

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
                parameter = { allParametersByName[it.name] ?: "undefined" },
                parameterByIndex = { allParametersByIndex[it.index] ?: "undefined" }
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

            val tempArray = "array${uniqueNumber.getAndIncrement()}"
            -"const $tempArray = "
            -arrayAccess.arrayExpression
            -";\n"
            val tempIndexes = arrayAccess.indexExpressions.map {
                val tempName = "index${uniqueNumber.getAndIncrement()}"
                -"const $tempName = "
                -it
                -";\n"
                tempName
            }

            val right: Any = if (typedRule.operationToken == KtTokens.EQ) typedRule.right!! else ValueOperator(
                left = VirtualArrayGet(tempArray, tempIndexes, arrayAccess.resolvedIndexedLvalueGet!!.resultingDescriptor, typedRule.getTsReceiver()),
                right = typedRule.right!!,
                functionDescriptor = typedRule.operationReference.resolvedReferenceTarget as FunctionDescriptor,
                dispatchReceiver = typedRule.getTsReceiver(),
                operationToken = typedRule.operationToken
            )
            val doubleReceiver =
                setFunction.dispatchReceiverParameter != null && setFunction.extensionReceiverParameter != null
            if (doubleReceiver) {
                -arrayAccess.getTsReceiver()
                -"."
            } else if (setFunction.dispatchReceiverParameter != null) {
                -tempArray
                -"."
            }
            -(setFunction.tsName ?: "set")
            -ArgumentsList(
                on = setFunction,
                prependArguments = if (doubleReceiver) listOf(arrayAccess.arrayExpression!!) else listOf(),
                orderedArguments = tempIndexes.map { it to null } + (right to null),
                namedArguments = listOf(),
                lambdaArgument = null
            )
        }
    )
    handle<KtBinaryExpression>(
        condition = {
            val arrayAccess = typedRule.left as? KtArrayAccessExpression ?: return@handle false
            val f = arrayAccess.resolvedIndexedLvalueSet?.resultingDescriptor ?: return@handle false
            (typedRule.resolvedVariableReassignment == true || typedRule.operationToken == KtTokens.EQ)
                    && replacements.getCall(f) != null
        },
        priority = 20_002,
        action = {
            val arrayAccess = typedRule.left as KtArrayAccessExpression
            val f = arrayAccess.resolvedIndexedLvalueSet!!.resultingDescriptor

            val reuseIdentifiers = typedRule.operationToken != KtTokens.EQ

            val tempArray: Any = if(reuseIdentifiers && !arrayAccess.arrayExpression!!.isSimple()) {
                val t = "array${uniqueNumber.getAndIncrement()}"
                -"const $t = "
                -arrayAccess.arrayExpression
                -";\n"
                t
            } else arrayAccess.arrayExpression!!
            val tempIndexes = if(reuseIdentifiers) {
                arrayAccess.indexExpressions.map {
                    if(it.isSimple()) {
                        it
                    } else {
                        val t = "index${uniqueNumber.getAndIncrement()}"
                        -"const $t = "
                        -it
                        -";\n"
                        t
                    }
                }
            } else {
                arrayAccess.indexExpressions
            }

            val right: Any = if (reuseIdentifiers) ValueOperator(
                left = VirtualArrayGet(tempArray, tempIndexes, arrayAccess.resolvedIndexedLvalueGet!!.resultingDescriptor, typedRule.getTsReceiver()),
                right = typedRule.right!!,
                functionDescriptor = typedRule.operationReference.resolvedReferenceTarget as FunctionDescriptor,
                dispatchReceiver = typedRule.getTsReceiver(),
                operationToken = typedRule.operationToken
            ) else typedRule.right!!
            val rule = replacements.getCall(f)!!

            val allParametersByIndex = HashMap<Int, Any>()
            val allParametersByName = HashMap<String, Any>()
            tempIndexes.forEachIndexed { index, valueArgument ->
                allParametersByIndex[index] = valueArgument
                f.valueParameters.getOrNull(index)?.name?.asString()?.let {
                    allParametersByName[it] = valueArgument
                }
            }
            right.let { valueArgument ->
                val index = f.valueParameters.lastIndex
                allParametersByIndex[index] = valueArgument
                f.valueParameters.getOrNull(index)?.name?.asString()?.let {
                    allParametersByName[it] = valueArgument
                }
                Unit
            }
            val typeParametersByName =
                typedRule.resolvedCall?.typeArguments?.mapKeys { it.key.name.asString() } ?: mapOf()
            val typeParametersByIndex = typedRule.resolvedCall?.typeArguments?.mapKeys { it.key.index } ?: mapOf()

            emitTemplate(
                requiresWrapping = typedRule.actuallyCouldBeExpression,
                template = rule.template,
                receiver = tempArray,
                dispatchReceiver = arrayAccess.getTsReceiver(),
                value = right,
                allParameters = ArrayList<Any?>().apply {
                    tempIndexes.forEachBetween(
                        forItem = { add(it) },
                        between = { add(", ") }
                    )
                },
                parameter = { allParametersByName[it.name] ?: "undefined" },
                typeParameter = { typeParametersByName[it.name] ?: "undefined" },
                parameterByIndex = { allParametersByIndex[it.index] ?: "undefined" },
                typeParameterByIndex = { typeParametersByIndex[it.index] ?: "undefined" }
            )
        }
    )

    //Operator
    handle<ValueOperator>(
        condition = {
            replacements.getCall(typedRule.functionDescriptor) != null
        },
        priority = 10_000,
        action = {
            val rule = replacements.getCall(typedRule.functionDescriptor)!!

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
                parameter = { right },
                parameterByIndex = { right }
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
        -(typedRule.functionDescriptor.tsName ?: typedRule.functionDescriptor.name.asString())
        -ArgumentsList(
            on = typedRule.functionDescriptor,
            prependArguments = if (typedRule.functionDescriptor.extensionReceiverParameter != null) listOf(left) else listOf(),
            orderedArguments = listOf(right to null),
            namedArguments = listOf(),
            lambdaArgument = null
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
                operationToken = typedRule.operationToken
            )
        })

    handle<KtPrefixExpression>(
        condition = {
            val f = typedRule.operationReference.resolvedReferenceTarget as? FunctionDescriptor ?: return@handle false
            replacements.getCall(f) != null
        },
        priority = 10_000,
        action = {
            val f = typedRule.operationReference.resolvedReferenceTarget as FunctionDescriptor
            val rule = replacements.getCall(f)!!

            emitTemplate(
                requiresWrapping = typedRule.actuallyCouldBeExpression,
                template = rule.template,
                receiver = typedRule.baseExpression,
                dispatchReceiver = if(f.extensionReceiverParameter != null) typedRule.getTsReceiver() else typedRule.baseExpression
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
            -(f.tsName ?: typedRule.operationReference.text)
            -ArgumentsList(
                on = f,
                prependArguments = if (f.extensionReceiverParameter != null) listOf(typedRule.baseExpression!!) else listOf(),
                orderedArguments = listOf(),
                namedArguments = listOf(),
                lambdaArgument = null
            )
        }
    )
}
