package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtAnnotation
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

//class TestThing(){
//    operator fun dec
//}

fun TypescriptTranslator.registerOperators() {

    val binaryFunctionNames = mapOf(
        KtTokens.EQ to null,
        KtTokens.PLUSEQ to "plusAssign",
        KtTokens.MINUSEQ to "minusAssign",
        KtTokens.MULTEQ to "timesAssign",
        KtTokens.DIVEQ to "divAssign",
        KtTokens.PERCEQ to "remAssign",
        KtTokens.PLUS to "plus",
        KtTokens.MINUS to "minus",
        KtTokens.MUL to "times",
        KtTokens.DIV to "div",
        KtTokens.PERC to "rem",
        KtTokens.RANGE to "rangeTo"
    )
    val unaryFunctionNames = mapOf(
        KtTokens.PLUS to "unaryPlus",
        KtTokens.MINUS to "unaryMinus",
        KtTokens.PLUSPLUS to "inc",
        KtTokens.MINUSMINUS to "dec"
    )

    handle<KtBinaryExpression>(
        condition = {
            val f = typedRule.operationReference.resolvedReferenceTarget as? FunctionDescriptor ?: return@handle false
            replacements.getCall(f) != null
        },
        priority = 10_000,
        action = {
            val f = typedRule.operationReference.resolvedReferenceTarget as FunctionDescriptor
            val rule = replacements.getCall(f)!!

            if (typedRule.operationToken == KtTokens.NOT_IN || typedRule.operationToken == KtTokens.EXCLEQ) {
                -"!("
            }

            val invertDirection =
                typedRule.operationToken == KtTokens.IN_KEYWORD || typedRule.operationToken == KtTokens.NOT_IN
            val left = if(invertDirection) typedRule.right else typedRule.left
            val right = if(invertDirection) typedRule.left else typedRule.right

            if (typedRule.resolvedVariableReassignment == true) {
                -left
                -" = "
            }
            rule.template.forEach { part ->
                when (part) {
                    is TemplatePart.Import -> out.addImport(part)
                    is TemplatePart.Text -> -part.string
                    TemplatePart.Receiver -> -left
                    TemplatePart.DispatchReceiver -> if (f.extensionReceiverParameter != null) -typedRule.getTsReceiver() else -left
                    TemplatePart.ExtensionReceiver -> -left
                    TemplatePart.AllParameters -> -right
                    TemplatePart.OperatorToken -> -typedRule.operationReference
                    is TemplatePart.Parameter -> -right
                    is TemplatePart.ParameterByIndex -> -right
                    is TemplatePart.TypeParameter -> -right
                    is TemplatePart.TypeParameterByIndex -> -right
                }
            }
            if (typedRule.operationToken == KtTokens.NOT_IN || typedRule.operationToken == KtTokens.EXCLEQ) {
                -")"
            }
        }
    )

    handle<KtBinaryExpression>(
        condition = { typedRule.operationReference.getReferencedNameElementType() != KtTokens.IDENTIFIER && typedRule.operationReference.resolvedReferenceTarget != null },
        priority = 1_000,
        action = {

            if (typedRule.operationToken == KtTokens.NOT_IN || typedRule.operationToken == KtTokens.EXCLEQ) {
                -"!("
            }

            val invertDirection =
                typedRule.operationToken == KtTokens.IN_KEYWORD || typedRule.operationToken == KtTokens.NOT_IN
            val left = if(invertDirection) typedRule.right else typedRule.left
            val right = if(invertDirection) typedRule.left else typedRule.right
            if (typedRule.resolvedVariableReassignment == true) {
                -left
                -" = "
            }
            val f = typedRule.operationReference.resolvedReferenceTarget as FunctionDescriptor
            val doubleReceiver = f.dispatchReceiverParameter != null && f.extensionReceiverParameter != null
            if (doubleReceiver) {
                -typedRule.getTsReceiver()
                -"."
            } else if (f.dispatchReceiverParameter != null) {
                -left
                -"."
            }
            -(f.tsName ?: typedRule.operationReference.text)
            -ArgumentsList(
                on = f,
                prependArguments = if (f.extensionReceiverParameter != null) listOf(left!!) else listOf(),
                orderedArguments = listOf(right!! to null),
                namedArguments = listOf(),
                lambdaArgument = null
            )
            if (typedRule.operationToken == KtTokens.NOT_IN || typedRule.operationToken == KtTokens.EXCLEQ) {
                -")"
            }
        }
    )

    handle<KtPrefixExpression>(
        condition = {
            val f = typedRule.operationReference.resolvedReferenceTarget as? FunctionDescriptor ?: return@handle false
            replacements.getCall(f) != null
        },
        priority = 10_000,
        action = {
            val f = typedRule.operationReference.resolvedReferenceTarget as FunctionDescriptor
            val rule = replacements.getCall(f)!!
            if (f.extensionReceiverParameter != null) {
                rule.template.forEach { part ->
                    when (part) {
                        is TemplatePart.Import -> out.addImport(part)
                        is TemplatePart.Text -> -part.string
                        TemplatePart.Receiver -> -typedRule.baseExpression
                        TemplatePart.DispatchReceiver -> -typedRule.getTsReceiver()
                        TemplatePart.ExtensionReceiver -> -typedRule.baseExpression
                    }
                }
            } else {
                rule.template.forEach { part ->
                    when (part) {
                        is TemplatePart.Import -> out.addImport(part)
                        is TemplatePart.Text -> -part.string
                        TemplatePart.Receiver -> -typedRule.baseExpression
                        TemplatePart.DispatchReceiver -> -typedRule.baseExpression
                        TemplatePart.ExtensionReceiver -> -typedRule.baseExpression
                    }
                }
            }
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
