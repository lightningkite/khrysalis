package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.util.AnalysisExtensions
import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.ValueDescriptor
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation
import org.jetbrains.kotlin.resolve.calls.components.isVararg
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


fun TypescriptTranslator.registerOperators() {


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
        -(f.tsNameOverridden ?: "get")
        -ArgumentsList(
            on = f,
            resolvedCall = typedRule.resolvedCall!!,
            prependArguments = if (doubleReceiver) listOf(typedRule.arrayExpression) else listOf(),
            replacements = (
                    typedRule.indexExpressions.mapIndexed { index, exp ->
                        typedRule.functionDescriptor.valueParameters.get(
                            index
                        ).let { it to exp }
                    }.associate { it }
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
                type = typedRule.resolvedCall?.getReturnType(),
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
                left = VirtualArrayGet(
                    arrayExpression = tempArray,
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
                -tempArray
                -"."
            }
            -(setFunction.tsNameOverridden ?: "set")
            -ArgumentsList(
                on = setFunction,
                resolvedCall = arrayAccess.resolvedIndexedLvalueSet!!,
                prependArguments = if (doubleReceiver) listOf(arrayAccess.arrayExpression!!) else listOf(),
                replacements = (
                        tempIndexes.mapIndexed { index, exp -> setFunction.valueParameters[index].let { it to exp } }
                            .associate { it }
                        ) + (arrayAccess.resolvedIndexedLvalueSet?.resultingDescriptor?.valueParameters?.lastOrNull()
                    ?.let { mapOf(it to right) } ?: mapOf())
            )
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
            -"/*bin $reuseIdentifiers*/"

            val tempArray: Any = if (reuseIdentifiers && !arrayAccess.arrayExpression!!.isSimple()) {
                val t = "array${uniqueNumber.getAndIncrement()}"
                -"const $t = "
                -arrayAccess.arrayExpression
                -";\n"
                t
            } else arrayAccess.arrayExpression!!
            val tempIndexes = if (reuseIdentifiers) {
                arrayAccess.indexExpressions.map {
                    if (it.isSimple()) {
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

            val right: Any = if (typedRule.operationToken != KtTokens.EQ) ValueOperator(
                left = VirtualArrayGet(
                    arrayExpression = tempArray,
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
            -"/* right is"
            -right
            -"*/"
            val rule = replacements.getCall(this@registerOperators, resolvedCall)!!
            emitTemplate(
                requiresWrapping = typedRule.actuallyCouldBeExpression,
                type = typedRule.resolvedExpressionTypeInfo?.type,
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
                parameter = resolvedCall.template_parameter,
                typeParameter = resolvedCall.template_typeParameter,
                parameterByIndex = {
                    if(it.index == arrayAccess.indexExpressions.size) right
                    else resolvedCall.template_parameterByIndex(it)
                },
                typeParameterByIndex = resolvedCall.template_typeParameterByIndex
            )
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
                type = typedRule.resolvedCall?.getReturnType(),
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
    handle<ValueOperator>(
        condition = { typedRule.operationToken == KtTokens.EQEQ },
        priority = 20
    ) {
        out.addImport("butterfly-web/dist/Kotlin", "safeEq")
        -"safeEq("
        -typedRule.left
        -", "
        -typedRule.right
        -")"
    }
    handle<ValueOperator>(
        condition = { typedRule.operationToken == KtTokens.EXCLEQ },
        priority = 20
    ) {
        out.addImport("butterfly-web/dist/Kotlin", "safeEq")
        -"!safeEq("
        -typedRule.left
        -", "
        -typedRule.right
        -")"
    }
    handle<ValueOperator>(
        condition = { typedRule.operationToken == KtTokens.EQEQ && (typedRule.left as? KtConstantExpression)?.text == "null" },
        priority = 200_000
    ) {
        -typedRule.right
        -" === null"
    }
    handle<ValueOperator>(
        condition = { typedRule.operationToken == KtTokens.EXCLEQ && (typedRule.left as? KtConstantExpression)?.text == "null" },
        priority = 210_000
    ) {
        -typedRule.right
        -" !== null"
    }
    handle<ValueOperator>(
        condition = { typedRule.operationToken == KtTokens.EQEQ && (typedRule.right as? KtConstantExpression)?.text == "null" },
        priority = 220_000
    ) {
        -typedRule.left
        -" === null"
    }
    handle<ValueOperator>(
        condition = { typedRule.operationToken == KtTokens.EXCLEQ && (typedRule.right as? KtConstantExpression)?.text == "null" },
        priority = 230_000
    ) {
        -typedRule.left
        -" !== null"
    }
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
        } else if (typedRule.functionDescriptor.dispatchReceiverParameter != null) {
            -left
            -"."
        }
        val funcName = typedRule.functionDescriptor.tsNameOverridden ?: typedRule.functionDescriptor.name.asString()
            .safeJsIdentifier()
        this.out.addImport(typedRule.functionDescriptor, funcName)
        -funcName
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
            typedRule.operationReference.getReferencedNameElementType() in setOf(
                KtTokens.GT,
                KtTokens.GTEQ,
                KtTokens.LT,
                KtTokens.LTEQ
            )
        },
        priority = 1,
        action = {
            -typedRule.left
            -".compareTo("
            -typedRule.right
            -") "
            val t = typedRule.operationToken
            if (t is KtSingleValueToken) -t.value
            -" 0"
        }
    )

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
                type = typedRule.resolvedExpressionTypeInfo?.type,
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
            -(f.tsNameOverridden ?: f.name.asString().safeJsIdentifier())
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
                type = typedRule.resolvedExpressionTypeInfo?.type,
                template = rule.template,
                receiver = typedRule.baseExpression,
                dispatchReceiver = if (f.candidateDescriptor.extensionReceiverParameter != null) typedRule.getTsReceiver() else typedRule.baseExpression
            )
        }
    )

    handle<KtPostfixExpression>(
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
            -(f.tsNameOverridden ?: f.name.asString().safeJsIdentifier())
            -ArgumentsList(
                on = f,
                resolvedCall = typedRule.resolvedCall!!,
                prependArguments = if (f.extensionReceiverParameter != null) listOf(typedRule.baseExpression!!) else listOf()
            )
        }
    )

    handle<KtPostfixExpression>(
        condition = {
            val sel =
                (typedRule.baseExpression as? KtQualifiedExpression)?.selectorExpression as? KtNameReferenceExpression
                    ?: typedRule.baseExpression as? KtNameReferenceExpression
                    ?: return@handle false
            typedRule.resolvedVariableReassignment == true && sel.resolvedReferenceTarget is ValueDescriptor
        },
        priority = 200_000,
        action = {
            val qual = typedRule.baseExpression as? KtQualifiedExpression
            val sel = qual?.selectorExpression as? KtNameReferenceExpression
                ?: typedRule.baseExpression as KtNameReferenceExpression
            val prop = sel.resolvedReferenceTarget as ValueDescriptor
            -VirtualSet(
                receiver = qual?.receiverExpression,
                nameReferenceExpression = sel,
                property = prop,
                receiverType = sel.resolvedExpressionTypeInfo?.type,
                expr = typedRule,
                safe = qual is KtSafeQualifiedExpression,
                value = ValueOperator(
                    left = VirtualGet(
                        receiver = qual?.receiverExpression,
                        nameReferenceExpression = sel,
                        property = prop,
                        receiverType = sel.resolvedExpressionTypeInfo?.type,
                        expr = typedRule,
                        safe = qual is KtSafeQualifiedExpression
                    ),
                    right = "1",
                    functionDescriptor = typedRule.resolvedCall!!.candidateDescriptor as FunctionDescriptor,
                    dispatchReceiver = typedRule.getTsReceiver(),
                    operationToken = when (typedRule.operationToken) {
                        KtTokens.PLUSPLUS -> KtTokens.PLUS
                        KtTokens.MINUSMINUS -> KtTokens.MINUS
                        else -> KtTokens.PLUS
                    },
                    resolvedCall = typedRule.resolvedCall
                )
            )
        }
    )
    handle<KtPrefixExpression>(
        condition = {
            val sel =
                (typedRule.baseExpression as? KtQualifiedExpression)?.selectorExpression as? KtNameReferenceExpression
                    ?: typedRule.baseExpression as? KtNameReferenceExpression
                    ?: return@handle false
            typedRule.resolvedVariableReassignment == true && sel.resolvedReferenceTarget is ValueDescriptor
        },
        priority = 200_000,
        action = {
            val qual = typedRule.baseExpression as? KtQualifiedExpression
            val sel = qual?.selectorExpression as? KtNameReferenceExpression
                ?: typedRule.baseExpression as KtNameReferenceExpression
            val prop = sel.resolvedReferenceTarget as ValueDescriptor
            -VirtualSet(
                receiver = qual?.receiverExpression,
                nameReferenceExpression = sel,
                property = prop,
                receiverType = sel.resolvedExpressionTypeInfo?.type,
                expr = typedRule,
                safe = qual is KtSafeQualifiedExpression,
                value = ValueOperator(
                    left = VirtualGet(
                        receiver = qual?.receiverExpression,
                        nameReferenceExpression = sel,
                        property = prop,
                        receiverType = sel.resolvedExpressionTypeInfo?.type,
                        expr = typedRule,
                        safe = qual is KtSafeQualifiedExpression
                    ),
                    right = "1",
                    functionDescriptor = typedRule.resolvedCall!!.candidateDescriptor as FunctionDescriptor,
                    dispatchReceiver = typedRule.getTsReceiver(),
                    operationToken = when (typedRule.operationToken) {
                        KtTokens.PLUSPLUS -> KtTokens.PLUS
                        KtTokens.MINUSMINUS -> KtTokens.MINUS
                        else -> KtTokens.PLUS
                    },
                    resolvedCall = typedRule.resolvedCall
                )
            )
        }
    )
}
