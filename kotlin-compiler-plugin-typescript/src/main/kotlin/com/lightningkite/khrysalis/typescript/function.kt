package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.codegen.findJavaDefaultArgumentValue
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.toVisibility
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierTypeOrDefault
import org.jetbrains.kotlin.resolve.descriptorUtil.declaresOrInheritsDefaultValue
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

private val FunctionDescriptor.tsName: String?
    get() = this.annotations
        .find { it.fqName?.asString()?.substringAfterLast('.') == "JsName" }
        ?.allValueArguments
        ?.entries
        ?.firstOrNull()
        ?.value
        ?.value
        ?.toString() ?: if (extensionReceiverParameter != null) {
        extensionReceiverParameter!!
            .value
            .type
            .getJetTypeFqName(false)
            .split('.')
            .joinToString("") { it.capitalize() } +
                this.name.identifier.capitalize()
    } else if(this.name.isSpecial) {
        null
    } else {
        this.name.identifier
    }

fun TypescriptTranslator.registerFunction() {
    handle<KtNamedFunction> {
        val isMember = typedRule.containingClassOrObject != null
        if (isMember) {
            -typedRule.visibilityModifierTypeOrDefault().toVisibility()
            -" "
        } else {
            -"function "
        }
        -(typedRule.resolvedFunction?.tsName ?: typedRule.nameIdentifier)
        -typedRule.typeParameterList
        fun afterParameters() {
            typedRule.typeReference?.let {
                -": "
                -it
            }
            val body = typedRule.bodyExpression
            if (body is KtBlockExpression) {
                -body
            } else {
                -"{ return "
                -body
                -"; }"
            }
        }
        typedRule.receiverTypeReference?.let { rType ->
            withReceiverScope(typedRule.resolvedFunction!!.fqNameSafe.asString()) { r ->
                -'('
                -r
                -": "
                -rType
                if (typedRule.valueParameters.isNotEmpty()) {
                    typedRule.valueParameters.forEach {
                        -", "
                        -it
                    }
                }
                -')'
                afterParameters()
            }
        } ?: run {
            -typedRule.valueParameterList
            afterParameters()
        }
    }

    handle<KtParameter>(
        condition = { typedRule.isVarArg },
        priority = 100,
        action = {
            - "..."
            -typedRule.nameIdentifier
            -": "
            -typedRule.typeReference
            -"[]"
        }
    )

    //TODO: Handle adding new for nested classes
    handle<KtCallExpression>(
        condition = { (typedRule.calleeExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget is ConstructorDescriptor && (typedRule.parent as? KtDotQualifiedExpression)?.selectorExpression != typedRule },
        priority = 2000,
        action = {
            -"new "
            doSuper()
        }
    )

    handle<KtDotQualifiedExpression>(
        condition = {
            (((typedRule.selectorExpression as? KtCallExpression)?.calleeExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? FunctionDescriptor)?.extensionReceiverParameter != null
        },
        priority = 1000,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val f = nre.resolvedReferenceTarget as FunctionDescriptor
            if (f.dispatchReceiverParameter != null) {
                -nre.getTsReceiver()
                -"."
            }
            -(f.tsName ?: nre.text)
            val withComments = callExp.valueArgumentList?.withComments() ?: listOf()
            -ArgumentsList(
                on = f,
                prependArguments = listOf(typedRule.receiverExpression),
                orderedArguments = withComments.filter { !it.first.isNamed() },
                namedArguments = withComments.filter { it.first.isNamed() },
                lambdaArgument = callExp.lambdaArguments.firstOrNull()
            )
        }
    )

    handle<KtCallExpression>(condition = { typedRule.calleeExpression is KtNameReferenceExpression }, priority = 1) {
        val withComments = typedRule.valueArgumentList?.withComments() ?: listOf()
        val nre = typedRule.calleeExpression as KtNameReferenceExpression
        val f = nre.resolvedReferenceTarget as FunctionDescriptor

        -(f.tsName ?: nre.text)
        -typedRule.typeArgumentList
        -ArgumentsList(
            on = f,
            prependArguments = listOf(),
            orderedArguments = withComments.filter { !it.first.isNamed() },
            namedArguments = withComments.filter { it.first.isNamed() },
            lambdaArgument = typedRule.lambdaArguments.firstOrNull()
        )
    }

    //Equivalents replacements
    handle<KtDotQualifiedExpression>(
        condition = {
            val desc = (((typedRule.selectorExpression as? KtCallExpression)?.calleeExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? FunctionDescriptor) ?: return@handle false
            replacements.getCall(desc) != null
        },
        priority = 10_000,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val f = nre.resolvedReferenceTarget as FunctionDescriptor
            val rule = replacements.getCall(f)!!

            val allParametersByIndex = HashMap<Int, KtValueArgument>()
            val allParametersByName = HashMap<String, KtValueArgument>()
            callExp.valueArguments.forEachIndexed { index, valueArgument ->
                if(valueArgument.name != null){
                    allParametersByName[valueArgument.name!!] = valueArgument
                    val i = f.valueParameters.indexOfFirst { it.name.asString() == valueArgument.name!! }
                    if(i != -1) {
                        allParametersByIndex[i] = valueArgument
                    }
                } else {
                    allParametersByIndex[index] = valueArgument
                    allParametersByName[f.valueParameters[index].name.asString()] = valueArgument
                }
            }
            val typeParametersByName = callExp.resolvedCall?.typeArguments?.mapKeys { it.key.name.asString() } ?: mapOf()
            val typeParametersByIndex = callExp.resolvedCall?.typeArguments?.mapKeys { it.key.index } ?: mapOf()

            rule.template.forEach { part ->
                when(part){
                    is TemplatePart.Text -> -part.string
                    TemplatePart.Receiver -> -typedRule.receiverExpression
                    TemplatePart.DispatchReceiver -> -nre.getTsReceiver()
                    TemplatePart.ExtensionReceiver -> -typedRule.receiverExpression
                    TemplatePart.Value -> { }
                    is TemplatePart.Parameter -> -(allParametersByName[part.name]?.getArgumentExpression())
                    is TemplatePart.ParameterByIndex -> -(allParametersByIndex[part.index]?.getArgumentExpression())
                    is TemplatePart.TypeParameter -> -(typeParametersByName[part.name])
                    is TemplatePart.TypeParameterByIndex -> -(typeParametersByIndex[part.index])
                }
            }
        }
    )

    handle<KtCallExpression>(
        condition = {
            val desc = (typedRule.calleeExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? FunctionDescriptor ?: return@handle false
            replacements.getCall(desc) != null
        },
        priority = 10_001,
        action = {
            val nre = typedRule.calleeExpression as KtNameReferenceExpression
            val f = nre.resolvedReferenceTarget as FunctionDescriptor
            val rule = replacements.getCall(f)!!

            val allParametersByIndex = HashMap<Int, KtValueArgument>()
            val allParametersByName = HashMap<String, KtValueArgument>()
            typedRule.valueArguments.forEachIndexed { index, valueArgument ->
                if(valueArgument.name != null){
                    allParametersByName[valueArgument.name!!] = valueArgument
                    val i = f.valueParameters.indexOfFirst { it.name.asString() == valueArgument.name!! }
                    if(i != -1) {
                        allParametersByIndex[i] = valueArgument
                    }
                } else {
                    allParametersByIndex[index] = valueArgument
                    f.valueParameters.getOrNull(index)?.name?.asString()?.let {
                        allParametersByName[it] = valueArgument
                    }
                }
            }
            val typeParametersByName = typedRule.resolvedCall?.typeArguments?.mapKeys { it.key.name.asString() } ?: mapOf()
            val typeParametersByIndex = typedRule.resolvedCall?.typeArguments?.mapKeys { it.key.index } ?: mapOf()

            rule.template.forEach { part ->
                when(part){
                    is TemplatePart.Text -> -part.string
                    TemplatePart.Receiver -> -nre.getTsReceiver()
                    TemplatePart.DispatchReceiver -> -nre.getTsReceiver()
                    TemplatePart.ExtensionReceiver -> { }
                    TemplatePart.Value -> { }
                    is TemplatePart.Parameter -> -(allParametersByName[part.name]?.getArgumentExpression())
                    is TemplatePart.ParameterByIndex -> -(allParametersByIndex[part.index]?.getArgumentExpression())
                    is TemplatePart.TypeParameter -> -(typeParametersByName[part.name])
                    is TemplatePart.TypeParameterByIndex -> -(typeParametersByIndex[part.index])
                }
            }
        }
    )

    handle<KtBinaryExpression>(
        condition = { typedRule.operationReference.getIdentifier()?.let{ it is LeafPsiElement && it.elementType == KtTokens.IDENTIFIER } == true },
        priority = 10_000,
        action = {
            val f = typedRule.operationReference.resolvedReferenceTarget as FunctionDescriptor
            val rule = replacements.getCall(f)!!
            if(f.extensionReceiverParameter != null){
                rule.template.forEach { part ->
                    when(part){
                        is TemplatePart.Text -> -part.string
                        TemplatePart.Receiver -> -typedRule.left
                        TemplatePart.DispatchReceiver -> -typedRule.getTsReceiver()
                        TemplatePart.ExtensionReceiver -> -typedRule.left
                        TemplatePart.Value -> { }
                        is TemplatePart.Parameter -> -typedRule.right
                        is TemplatePart.ParameterByIndex -> -typedRule.right
                        is TemplatePart.TypeParameter -> -typedRule.right
                        is TemplatePart.TypeParameterByIndex -> -typedRule.right
                    }
                }
            } else {
                rule.template.forEach { part ->
                    when(part){
                        is TemplatePart.Text -> -part.string
                        TemplatePart.Receiver -> -typedRule.left
                        TemplatePart.DispatchReceiver -> -typedRule.left
                        TemplatePart.ExtensionReceiver -> -typedRule.left
                        TemplatePart.Value -> { }
                        is TemplatePart.Parameter -> -typedRule.right
                        is TemplatePart.ParameterByIndex -> -typedRule.right
                        is TemplatePart.TypeParameter -> -typedRule.right
                        is TemplatePart.TypeParameterByIndex -> -typedRule.right
                    }
                }
            }
        }
    )

    handle<ArgumentsList> {
        -'('
        var first = true
        for (item in typedRule.prependArguments) {
            if (first) {
                first = false
            } else {
                -", "
            }
            -item
        }
        var currentOrderedIndex = 0
        val parameters = typedRule.on.valueParameters
        parameters.forEachIndexed { index, valueParam ->
            if (first) {
                first = false
            } else {
                -", "
            }
            fun emitParam(v: Pair<KtValueArgument, PsiComment?>) {
                -v.first.getArgumentExpression()
                v.second?.let {
                    -"/*"
                    -it.text.removePrefix("//").removePrefix("/*").removeSuffix("*/")
                    -"*/"
                }
            }
            if (currentOrderedIndex < typedRule.orderedArguments.size) {
                val v = typedRule.orderedArguments[currentOrderedIndex++]
                emitParam(v)
            } else {
                val v =
                    typedRule.namedArguments.find { it.first.getArgumentName()!!.text == valueParam.name.identifier }
                if (v == null) {
                    if (index == parameters.lastIndex && typedRule.lambdaArgument != null) {
                        -typedRule.lambdaArgument!!.getLambdaExpression()
                    } else {
                        -"undefined"
                    }
                } else emitParam(v)
            }
        }
        -')'
    }
}

fun KtValueArgumentList.withComments(): List<Pair<KtValueArgument, PsiComment?>> {
    val result = ArrayList<Pair<KtValueArgument, PsiComment?>>()
    var lastArg: KtValueArgument? = null
    var lastComment: PsiComment? = null
    for (item in allChildren) {
        when (item) {
            is KtValueArgument -> {
                if (lastArg != null) {
                    result.add(lastArg to lastComment)
                }
                lastComment = null
                lastArg = item
            }
            is PsiComment -> {
                if (lastArg != null) {
                    lastComment = item
                }
            }
        }
    }
    if (lastArg != null) {
        result.add(lastArg to lastComment)
    }
    return result
}

data class ArgumentsList(
    val on: FunctionDescriptor,
    val prependArguments: List<Any>,
    val orderedArguments: List<Pair<KtValueArgument, PsiComment?>>,
    val namedArguments: List<Pair<KtValueArgument, PsiComment?>>,
    val lambdaArgument: KtLambdaArgument?
) {

}