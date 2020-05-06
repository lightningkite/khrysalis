package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.backend.common.serialization.findSourceFile
import org.jetbrains.kotlin.backend.common.serialization.metadata.extractFileId
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.codegen.findJavaDefaultArgumentValue
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.resolve.descriptorUtil.classValueType
import org.jetbrains.kotlin.resolve.descriptorUtil.declaresOrInheritsDefaultValue
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

//TODO: Local function edgecase - the meaning of 'this' changes

val FunctionDescriptor.tsName: String?
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
    } else if (this.name.isSpecial) {
        null
    } else {
        this.name.identifier
    }

val FunctionDescriptor.tsDefaultName: String?
    get() {
        return (this.containingDeclaration as? ClassDescriptor ?: return null)
            .fqNameSafe.asString()
            .split('.')
            .joinToString("") { it.capitalize() }
            .plus(tsName?.capitalize() ?: return null)
            .decapitalize()
    }

fun TypescriptTranslator.registerFunction() {
    handle<KtNamedFunction>(
        condition = { typedRule.parentOfType<KtClassBody>()?.parentOfType<KtClass>()?.isInterface() == true },
        priority = 100,
        action = {
            -(typedRule.resolvedFunction?.tsName ?: typedRule.nameIdentifier)
            -typedRule.typeParameterList
            fun afterParameters() {
                typedRule.typeReference?.let {
                    -": "
                    -it
                }
                val tr = typedRule
                val ktClass = typedRule.parentOfType<KtClassBody>()!!.parentOfType<KtClass>()!!
                ktClass.addPostAction {
                    -"\n"
                    if (ktClass.isPublic) {
                        -"export "
                    }
                    -"function "
                    val fname = tr.resolvedFunction?.tsDefaultName
                        ?: throw IllegalStateException("Cannot write default method without name.")
                    -fname
                    (ktClass.typeParameters + tr.typeParameters).takeUnless { it.isEmpty() }?.let {
                        -'<'
                        it.forEachBetween(
                            forItem = { -it },
                            between = { -", " }
                        )
                        -'>'
                    }
                    withReceiverScope(tr.resolvedFunction!!.fqNameSafe.asString()) { r ->
                        -'('
                        -r
                        -": "
                        -ktClass.nameIdentifier
                        ktClass.typeParameterList?.let {
                            -'<'
                            it.parameters.forEachBetween(
                                forItem = { -it.name },
                                between = { -", " }
                            )
                            -'>'
                        }
                        if (tr.valueParameters.isNotEmpty()) {
                            tr.valueParameters.forEach {
                                -", "
                                -it
                            }
                        }
                        -')'
                        val body = tr.bodyExpression
                        if (body is KtBlockExpression) {
                            -body
                        } else {
                            -"{ return "
                            -body
                            -"; }"
                        }
                    }
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
    )
    handle<KtNamedFunction> {
        val isMember = typedRule.containingClassOrObject != null
        if (isMember) {
            if (typedRule.parentOfType<KtClassBody>()?.parentOfType<KtClass>()?.isInterface() == false) {
                -(typedRule.visibilityModifier() ?: "public")
            }
            -" "
        } else {
            if(typedRule.isTopLevel() && !typedRule.isPrivate()) -"export "
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

    handle<KtParameter> {
        -typedRule.allChildren
            .filter { it !is LeafPsiElement || (it.elementType != KtTokens.VAL_KEYWORD && it.elementType != KtTokens.VAR_KEYWORD) }
    }

    handle<KtParameter>(
        condition = { typedRule.isVarArg },
        priority = 100,
        action = {
            -"..."
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
            out.addImport(f, f.tsName)
            if (f.dispatchReceiverParameter != null) {
                -nre.getTsReceiver()
                -"."
            }
            -(f.tsName ?: nre.text)
            val withComments = callExp.valueArgumentList?.withComments() ?: listOf()
            -ArgumentsList(
                on = f,
                prependArguments = listOf(typedRule.receiverExpression),
                orderedArguments = withComments.filter { !it.first.isNamed() }
                    .map { it.first.getArgumentExpression()!! to it.second },
                namedArguments = withComments.filter { it.first.isNamed() },
                lambdaArgument = callExp.lambdaArguments.firstOrNull()
            )
        }
    )

    handle<KtCallExpression>(
        condition = { (typedRule.calleeExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget is FunctionDescriptor },
        priority = 1
    ) {
        val withComments = typedRule.valueArgumentList?.withComments() ?: listOf()
        val nre = typedRule.calleeExpression as KtNameReferenceExpression
        val f = nre.resolvedReferenceTarget as FunctionDescriptor
        out.addImport(f, f.tsName)

        -(f.tsName ?: nre.text)
        -typedRule.typeArgumentList
        -ArgumentsList(
            on = f,
            prependArguments = listOf(),
            orderedArguments = withComments.filter { !it.first.isNamed() }
                .map { it.first.getArgumentExpression()!! to it.second },
            namedArguments = withComments.filter { it.first.isNamed() },
            lambdaArgument = typedRule.lambdaArguments.firstOrNull()
        )
    }

    //infix
    handle<KtBinaryExpression>(
        condition = { typedRule.operationReference.getIdentifier() != null },
        priority = 1_000,
        action = {
            val f = typedRule.operationReference.resolvedReferenceTarget as FunctionDescriptor
            out.addImport(f, f.tsName)
            val doubleReceiver = f.dispatchReceiverParameter != null && f.extensionReceiverParameter != null
            if (doubleReceiver) {
                -typedRule.getTsReceiver()
                -"."
            } else if (f.dispatchReceiverParameter != null) {
                -typedRule.left
                -"."
            }
            -(f.tsName ?: typedRule.operationReference.text)
            -ArgumentsList(
                on = f,
                prependArguments = if (f.extensionReceiverParameter != null) listOf(typedRule.left!!) else listOf(),
                orderedArguments = listOf(typedRule.right!! to null),
                namedArguments = listOf(),
                lambdaArgument = null
            )
        }
    )
    handle<KtBinaryExpression>(
        condition = {
            if(typedRule.operationReference.getIdentifier() == null) return@handle false
            val f = typedRule.operationReference.resolvedReferenceTarget as? FunctionDescriptor ?: return@handle false
            replacements.getCall(f) != null
        },
        priority = 10_000,
        action = {
            val f = typedRule.operationReference.resolvedReferenceTarget as FunctionDescriptor
            val rule = replacements.getCall(f)!!

            val allParametersByIndex = mapOf<Int, Any>(0 to typedRule.right!!)
            val allParametersByName = mapOf<String, Any>(f.valueParameters.first().name.asString() to typedRule.right!!)
            rule.template.forEach { part ->
                when (part) {
                    is TemplatePart.Import -> out.addImport(part)
                    is TemplatePart.Text -> -part.string
                    TemplatePart.Receiver -> -(typedRule.left!!)
                    TemplatePart.DispatchReceiver -> -(typedRule.operationReference.getTsReceiver() ?: typedRule.left!!)
                    TemplatePart.ExtensionReceiver -> -(typedRule.left!!)
                    TemplatePart.AllParameters -> -allParametersByIndex[0]
                    is TemplatePart.Parameter -> -allParametersByName[part.name]
                    is TemplatePart.ParameterByIndex -> -allParametersByIndex[part.index]
                }
            }
        }
    )

    //Equivalents replacements
    handle<KtDotQualifiedExpression>(
        condition = {
            val desc =
                (((typedRule.selectorExpression as? KtCallExpression)?.calleeExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? FunctionDescriptor)
                    ?: return@handle false
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
                if (valueArgument.name != null) {
                    allParametersByName[valueArgument.name!!] = valueArgument
                    val i = f.valueParameters.indexOfFirst { it.name.asString() == valueArgument.name!! }
                    if (i != -1) {
                        allParametersByIndex[i] = valueArgument
                    }
                } else {
                    allParametersByIndex[index] = valueArgument
                    allParametersByName[f.valueParameters[index].name.asString()] = valueArgument
                }
            }
            val typeParametersByName =
                callExp.resolvedCall?.typeArguments?.mapKeys { it.key.name.asString() } ?: mapOf()
            val typeParametersByIndex = callExp.resolvedCall?.typeArguments?.mapKeys { it.key.index } ?: mapOf()

            rule.template.forEach { part ->
                when (part) {
                    is TemplatePart.Import -> out.addImport(part)
                    is TemplatePart.Text -> -part.string
                    TemplatePart.Receiver -> -typedRule.receiverExpression
                    TemplatePart.DispatchReceiver -> -nre.getTsReceiver()
                    TemplatePart.ExtensionReceiver -> -typedRule.receiverExpression
                    TemplatePart.AllParameters -> callExp.valueArguments.forEachBetween(
                        forItem = { -it },
                        between = { -", " }
                    )
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
            val desc =
                (typedRule.calleeExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? FunctionDescriptor
                    ?: return@handle false
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
                if (valueArgument.name != null) {
                    allParametersByName[valueArgument.name!!] = valueArgument
                    val i = f.valueParameters.indexOfFirst { it.name.asString() == valueArgument.name!! }
                    if (i != -1) {
                        allParametersByIndex[i] = valueArgument
                    }
                } else {
                    allParametersByIndex[index] = valueArgument
                    f.valueParameters.getOrNull(index)?.name?.asString()?.let {
                        allParametersByName[it] = valueArgument
                    }
                }
            }
            val typeParametersByName =
                typedRule.resolvedCall?.typeArguments?.mapKeys { it.key.name.asString() } ?: mapOf()
            val typeParametersByIndex = typedRule.resolvedCall?.typeArguments?.mapKeys { it.key.index } ?: mapOf()

            rule.template.forEach { part ->
                when (part) {
                    is TemplatePart.Import -> out.addImport(part)
                    is TemplatePart.Text -> -part.string
                    TemplatePart.Receiver -> -nre.getTsReceiver()
                    TemplatePart.DispatchReceiver -> -nre.getTsReceiver()
                    TemplatePart.AllParameters -> typedRule.valueArguments.forEachBetween(
                        forItem = { -it },
                        between = { -", " }
                    )
                    is TemplatePart.Parameter -> -(allParametersByName[part.name]?.getArgumentExpression())
                    is TemplatePart.ParameterByIndex -> -(allParametersByIndex[part.index]?.getArgumentExpression())
                    is TemplatePart.TypeParameter -> -(typeParametersByName[part.name])
                    is TemplatePart.TypeParameterByIndex -> -(typeParametersByIndex[part.index])
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
            fun emitParam(v: Pair<Any, PsiComment?>) {
                -v.first
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
                } else emitParam(v.first.getArgumentExpression()!! to v.second)
            }
        }
        for (item in typedRule.appendArguments) {
            if (first) {
                first = false
            } else {
                -", "
            }
            -item
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
    val prependArguments: List<Any> = listOf(),
    val appendArguments: List<Any> = listOf(),
    val orderedArguments: List<Pair<Any, PsiComment?>> = listOf(),
    val namedArguments: List<Pair<KtValueArgument, PsiComment?>> = listOf(),
    val lambdaArgument: KtLambdaArgument? = null
) {

}