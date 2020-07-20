package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.lexer.KtToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedValueArgument

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

data class OpInfo(
    val name: String,
    val binary: Boolean,
    val swiftToken: String,
    val kotlinToken: KtToken,
    val pureKotlinToken: KtToken? = null
)

val operators = listOf(
    OpInfo(name = "plus", binary = true, swiftToken = "+", kotlinToken = KtTokens.PLUS),
    OpInfo(name = "minus", binary = true, swiftToken = "-", kotlinToken = KtTokens.MINUS),
    OpInfo(name = "times", binary = true, swiftToken = "*", kotlinToken = KtTokens.MUL),
    OpInfo(name = "div", binary = true, swiftToken = "/", kotlinToken = KtTokens.DIV),
    OpInfo(name = "rem", binary = true, swiftToken = "%", kotlinToken = KtTokens.PERC),
    OpInfo(name = "plusAssign", binary = true, swiftToken = "+=", kotlinToken = KtTokens.PLUSEQ, pureKotlinToken = KtTokens.PLUS),
    OpInfo(name = "minusAssign", binary = true, swiftToken = "-=", kotlinToken = KtTokens.MINUSEQ, pureKotlinToken = KtTokens.MINUS),
    OpInfo(name = "timesAssign", binary = true, swiftToken = "*=", kotlinToken = KtTokens.MULTEQ, pureKotlinToken = KtTokens.MUL),
    OpInfo(name = "divAssign", binary = true, swiftToken = "/=", kotlinToken = KtTokens.DIVEQ, pureKotlinToken = KtTokens.DIV),
    OpInfo(name = "remAssign", binary = true, swiftToken = "%=", kotlinToken = KtTokens.PERCEQ, pureKotlinToken = KtTokens.PERC),
    OpInfo(name = "unaryMinus", binary = false, swiftToken = "-", kotlinToken = KtTokens.MINUS),
    OpInfo(name = "unaryPlus", binary = false, swiftToken = "+", kotlinToken = KtTokens.PLUS),
    OpInfo(name = "not", binary = false, swiftToken = "!", kotlinToken = KtTokens.EXCL)
)
val operatorsByName = operators.associateBy { it.name }

val FunctionDescriptor.swiftOperatorPossible: Boolean
    get() = isOperator && (dispatchReceiverParameter == null) != (extensionReceiverParameter == null) && typeParameters.isEmpty() && operatorsByName[name.asString()] != null

fun SwiftTranslator.registerOperators() {
    handle<VirtualFunction>(
        condition = {
            typedRule.resolvedFunction?.swiftOperatorPossible == true
        },
        priority = 10
    ) {
        doSuper()
        1 until 20
        val opName = typedRule.resolvedFunction?.name?.asString()
        val (_, isBinary, operator) = operatorsByName[opName]!!
        val useStatic = true
        val rType = typedRule.resolvedFunction?.dispatchReceiverParameter?.type
            ?: typedRule.resolvedFunction?.extensionReceiverParameter?.type
        if (isBinary) {
            -"\n"
            if (useStatic) {
                -"static "
            }
            -"func $operator(receiver: "
            -rType
            -", value: "
            -typedRule.resolvedFunction?.valueParameters?.firstOrNull()?.type
            -") -> "
            -typedRule.resolvedFunction?.returnType
            -" { receiver."
            -opName
            -"("
            -typedRule.resolvedFunction?.valueParameters?.firstOrNull()?.name?.asString()
            -": value) }"
        } else {
            -"\n"
            if (useStatic) {
                -"static "
            }
            -"prefix func $operator(receiver: "
            -rType
            -") -> "
            -typedRule.resolvedFunction?.returnType
            -" { receiver."
            -opName
            -"() }"
        }
    }

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
            prependArguments = if (doubleReceiver) listOf(typedRule.arrayExpression) else listOf(),
            replacements = (
                typedRule.indexExpressions.mapIndexed { index, exp -> typedRule.functionDescriptor.valueParameters.get(index).let { it to exp } }.associate { it }
            )
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
                if(it.isSimple()){
                    it
                } else {
                    val tempName = "index${uniqueNumber.getAndIncrement()}"
                    -"let $tempName = "
                    -it
                    -"\n"
                    tempName
                }
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
                prependArguments = if (doubleReceiver) listOf(arrayAccess.arrayExpression!!) else listOf(),
                replacements = (
                        tempIndexes.mapIndexed { index, exp -> setFunction.valueParameters[index].let { it to exp } }.associate { it }
                        ) + (arrayAccess.resolvedIndexedLvalueSet?.resultingDescriptor?.valueParameters?.lastOrNull()?.let { mapOf(it to right) } ?: mapOf())
            )
            if (needsClose) {
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
            val tempIndexes = if (reuseIdentifiers) {
                arrayAccess.indexExpressions.map {
                    if (it.isSimple()) {
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
            if (needsClose) {
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
    handle<ValueOperator>(
        condition = {
            typedRule.functionDescriptor.swiftOperatorPossible
        },
        priority = 10,
        action = {
            -typedRule.left
            -' '
            -operatorsByName[typedRule.functionDescriptor.name.asString()]?.swiftToken
            -' '
            -typedRule.right
        }
    )
    handle<KtBinaryExpression>(
        condition = {
            typedRule.operationReference.getReferencedNameElementType() != KtTokens.IDENTIFIER
                    && typedRule.operationReference.getReferencedNameElementType() != KtTokens.EQ
                    && typedRule.operationReference.resolvedReferenceTarget is FunctionDescriptor
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
            typedRule.operationReference.getReferencedNameElementType() == KtTokens.EQEQ
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
                dispatchReceiver = if (f.candidateDescriptor.extensionReceiverParameter != null) typedRule.getTsReceiver() else typedRule.baseExpression
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
                dispatchReceiver = if (f.candidateDescriptor.extensionReceiverParameter != null) typedRule.getTsReceiver() else typedRule.baseExpression
            )
        }
    )
}