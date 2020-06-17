package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.typescript.manifest.declaresPrefix
import com.lightningkite.khrysalis.util.forEachBetween
import com.lightningkite.khrysalis.util.simpleFqName
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.resolve.calls.components.isVararg
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall

//TODO: Local function edgecase - the meaning of 'this' changes

val FunctionDescriptor.tsNameOverridden: String?
    get() = if (this is ConstructorDescriptor) {
        if (!this.isPrimary) {
            this.constructedClass.name.asString() + "." + this.tsConstructorName
        } else null
    } else this.annotations
        .find { it.fqName?.asString()?.substringAfterLast('.') == "JsName" }
        ?.allValueArguments
        ?.entries
        ?.firstOrNull()
        ?.value
        ?.value
        ?.toString()?.safeJsIdentifier() ?: if (extensionReceiverParameter != null) {
        extensionReceiverParameter!!
            .value
            .type
            .getJetTypeFqName(false)
            .split('.')
            .joinToString("") { it.capitalize() }.decapitalize() +
                this.name.identifier.capitalize()
    } else this.overriddenDescriptors.asSequence().mapNotNull { it.tsNameOverridden }.firstOrNull()

val FunctionDescriptor.tsName: String?
    get() = tsNameOverridden ?: if (this.name.isSpecial) {
        null
    } else {
        this.name.identifier
    }

val FunctionDescriptor.tsDefaultName: String?
    get() = tsName

data class VirtualFunction(
    val name: Any,
    val resolvedFunction: FunctionDescriptor? = null,
    val typeParameters: List<Any>,
    val valueParameters: List<Any>,
    val returnType: Any,
    val body: KtExpression?
)

fun TypescriptTranslator.registerFunction() {
    handle<VirtualFunction> {
        -typedRule.name
        typedRule.typeParameters.takeUnless { it.isEmpty() }?.let {
            -'<'
            it.forEachBetween(
                forItem = { -it },
                between = { -", " }
            )
            -'>'
        }
        -'('
        typedRule.valueParameters.forEachBetween(
            forItem = { -it },
            between = { -", " }
        )
        -')'
        -": "
        -typedRule.returnType
        -' '
        val body = typedRule.body
        when (body) {
            null -> {
            }
            is KtBlockExpression -> {
                -body
            }
            else -> {
                -"{ return "
                -body
                -"; }"
            }
        }

    }
    handle<KtNamedFunction>(
        condition = { typedRule.parentOfType<KtClassBody>()?.parentOfType<KtClass>()?.isInterface() == true },
        priority = 100,
        action = {
            if (typedRule.receiverTypeReference != null) throw IllegalArgumentException("Receivers on interface methods aren't supported yet.")
            val mainDecl = VirtualFunction(
                name = typedRule.resolvedFunction?.tsNameOverridden ?: typedRule.nameIdentifier!!,
                resolvedFunction = typedRule.resolvedFunction,
                typeParameters = typedRule.typeParameters,
                valueParameters = (typedRule.typeParameters.filter { it.hasModifier(KtTokens.REIFIED_KEYWORD) }
                    .map { listOf(it.name, ": any") })
                    .plus(typedRule.valueParameters),
                returnType = typedRule.typeReference
                    ?: typedRule.bodyExpression?.takeUnless { it is KtBlockExpression }?.resolvedExpressionTypeInfo?.type
                    ?: "void",
                body = null
            )
            -mainDecl
            if (typedRule.bodyExpression != null) {
                val tr = typedRule
                val ktClassBody = typedRule.parentOfType<KtClassBody>()!!
                val ktClass = ktClassBody.parentOfType<KtClass>()!!
                ktClassBody.addPostAction {
                    withReceiverScope(tr.containingClass()!!.resolvedClass!!) { rName2 ->
                        val recParam = listOf<Any>(
                            rName2,
                            ": ",
                            tr.containingClass()?.let { it.nameIdentifier } ?: "any"
                        )
                        -"\nexport function "
                        -VirtualFunction(
                            name = tr.resolvedFunction!!.tsDefaultName!!,
                            resolvedFunction = tr.resolvedFunction,
                            typeParameters = ktClass.typeParameters + mainDecl.typeParameters,
                            valueParameters = listOf<Any>(recParam) + mainDecl.valueParameters,
                            returnType = mainDecl.returnType,
                            body = tr.bodyExpression
                        )
                    }
                }
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
            if (typedRule.hasModifier(KtTokens.ABSTRACT_KEYWORD)) {
                -"abstract "
            }
        } else {
            if (typedRule.isTopLevel() && !typedRule.isPrivate()) {
                -"$declaresPrefix${typedRule.simpleFqName}\n"
                -"export "
            }
            -"function "
        }
        fun emit(rName: String? = null) {
            -VirtualFunction(
                name = typedRule.resolvedFunction?.tsNameOverridden ?: typedRule.nameIdentifier!!,
                resolvedFunction = typedRule.resolvedFunction,
                typeParameters = typedRule.typeParameters,
                valueParameters = (typedRule.receiverTypeReference?.let {
                    listOf(listOf(rName, ": ", it))
                } ?: listOf())
                    .plus(typedRule.typeParameters.filter { it.hasModifier(KtTokens.REIFIED_KEYWORD) }
                        .map { listOf(it.name, ": any") })
                    .plus(typedRule.valueParameters),
                returnType = typedRule.typeReference
                    ?: typedRule.bodyExpression?.takeUnless { it is KtBlockExpression }?.resolvedExpressionTypeInfo?.type
                    ?: "void",
                body = typedRule.bodyExpression
            )
        }
        typedRule.receiverTypeReference?.let {
            withReceiverScope(typedRule.resolvedFunction!!) { rName ->
                emit(rName)
            }
        } ?: run {
            emit(null)
        }
    }

    handle<KtParameter> {
        -typedRule.nameIdentifier
        typedRule.typeReference?.let {
            -": "
            -it
        }
        typedRule.defaultValue?.let {
            -" = "
            -it
        }
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

    handle<KtCallExpression>(
        condition = {
            (typedRule.calleeExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget.let { it is ConstructorDescriptor && it.isPrimary }
                    && (typedRule.parent as? KtDotQualifiedExpression)?.selectorExpression != typedRule
        },
        priority = 2000,
        action = {
            -"new "
            doSuper()
        }
    )
    handle<KtDotQualifiedExpression>(
        condition = {
            var current = typedRule
            while (current.selectorExpression is KtDotQualifiedExpression) {
                current = current.selectorExpression as KtDotQualifiedExpression
            }
            val callExp = current.selectorExpression as? KtCallExpression ?: return@handle false
            val nre = callExp.calleeExpression as? KtNameReferenceExpression ?: return@handle false
            nre.resolvedReferenceTarget.let { it is ConstructorDescriptor && it.isPrimary }
        },
        priority = 2000,
        action = {
            -"new "
            doSuper()
        }
    )

    handle<KtDotQualifiedExpression>(
        condition = {
            ((typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.candidateDescriptor as? FunctionDescriptor)?.extensionReceiverParameter != null
        },
        priority = 1000,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val f = callExp.resolvedCall!!.candidateDescriptor as FunctionDescriptor
            out.addImport(f, f.tsName)

            -nre
            val withComments = callExp.valueArgumentList?.withComments() ?: listOf()
            -ArgumentsList(
                on = f,
                resolvedCall = callExp.resolvedCall,
                prependArguments = listOf(typedRule.receiverExpression),
                orderedArguments = withComments.filter { !it.first.isNamed() }
                    .map { it.first.getArgumentExpression()!! to it.second },
                namedArguments = withComments.filter { it.first.isNamed() },
                lambdaArgument = callExp.lambdaArguments.firstOrNull()
            )
        }
    )

    handle<KtSafeQualifiedExpression>(
        condition = {
            typedRule.actuallyCouldBeExpression &&
                    ((typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.candidateDescriptor as? FunctionDescriptor)?.extensionReceiverParameter != null
        },
        priority = 1002,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val f = callExp.resolvedCall!!.candidateDescriptor as FunctionDescriptor
            out.addImport(f, f.tsName)

            -"((_it)=>{\n"
            -"if(_it === null) return null;\nreturn "
            -nre
            val withComments = callExp.valueArgumentList?.withComments() ?: listOf()
            -ArgumentsList(
                on = f,
                resolvedCall = callExp.resolvedCall,
                prependArguments = listOf("_it"),
                orderedArguments = withComments.filter { !it.first.isNamed() }
                    .map { it.first.getArgumentExpression()!! to it.second },
                namedArguments = withComments.filter { it.first.isNamed() },
                lambdaArgument = callExp.lambdaArguments.firstOrNull()
            )
            -"\n})("
            -typedRule.receiverExpression
            -')'
        }
    )

    handle<KtSafeQualifiedExpression>(
        condition = {
            ((typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.candidateDescriptor as? FunctionDescriptor)?.extensionReceiverParameter != null
        },
        priority = 1001,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val f = nre.resolvedReferenceTarget as FunctionDescriptor
            out.addImport(f, f.tsName)

            val tempName = "temp${uniqueNumber.getAndIncrement()}"
            -"const $tempName = "
            -typedRule.receiverExpression
            -";\nif($tempName !== null) "
            -nre
            val withComments = callExp.valueArgumentList?.withComments() ?: listOf()
            -ArgumentsList(
                on = f,
                resolvedCall = callExp.resolvedCall,
                prependArguments = listOf(tempName),
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
        if (f is ConstructorDescriptor) {
//            if (f.constructedClass.let { it.tsTopLevelMessedUp || it.containingDeclaration !is ClassDescriptor }) {
//                out.addImport(f.constructedClass, f.constructedClass.tsTopLevelName)
//            }
        } else {
            out.addImport(f, f.tsName)
        }

        -nre
        -typedRule.typeArgumentList
        -ArgumentsList(
            on = f,
            resolvedCall = typedRule.resolvedCall,
            prependArguments = listOf(),
            orderedArguments = withComments.filter { !it.first.isNamed() }
                .map { it.first.getArgumentExpression()!! to it.second },
            namedArguments = withComments.filter { it.first.isNamed() },
            lambdaArgument = typedRule.lambdaArguments.firstOrNull()
        )
    }

    handle<KtNameReferenceExpression>(
        condition = { (typedRule.resolvedReferenceTarget as? FunctionDescriptor)?.tsNameOverridden != null },
        priority = 50,
        action = {
            -(typedRule.resolvedReferenceTarget as FunctionDescriptor).tsNameOverridden!!
        }
    )

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
            -(f.tsNameOverridden ?: typedRule.operationReference.text)
            -ArgumentsList(
                on = f,
                resolvedCall = typedRule.resolvedCall,
                prependArguments = if (f.extensionReceiverParameter != null) listOf(typedRule.left!!) else listOf(),
                orderedArguments = listOf(typedRule.right!! to null),
                namedArguments = listOf(),
                lambdaArgument = null
            )
        }
    )
    handle<KtBinaryExpression>(
        condition = {
            if (typedRule.operationReference.getIdentifier() == null) return@handle false
            replacements.getCall(typedRule.resolvedCall ?: return@handle false) != null
        },
        priority = 10_000,
        action = {
            val resolvedCall = typedRule.resolvedCall!!
            val rule = replacements.getCall(resolvedCall)!!

            emitTemplate(
                requiresWrapping = typedRule.actuallyCouldBeExpression,
                template = rule.template,
                receiver = typedRule.left,
                dispatchReceiver = typedRule.operationReference.getTsReceiver() ?: typedRule.left,
                allParameters = typedRule.right,
                parameter = resolvedCall.template_parameter,
                typeParameter = resolvedCall.template_typeParameter,
                parameterByIndex = resolvedCall.template_parameterByIndex,
                typeParameterByIndex = resolvedCall.template_typeParameterByIndex
            )
        }
    )

    //Equivalents replacements
    handle<KtDotQualifiedExpression>(
        condition = {
            val callExp = typedRule.selectorExpression as? KtCallExpression ?: return@handle false
            replacements.getCall(callExp.resolvedCall ?: return@handle false) != null
        },
        priority = 10_000,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val resolvedCall = callExp.resolvedCall!!
            val rule = replacements.getCall(resolvedCall)!!

            emitTemplate(
                requiresWrapping = typedRule.actuallyCouldBeExpression,
                template = rule.template,
                receiver = typedRule.receiverExpression,
                dispatchReceiver = nre.getTsReceiver(),
                extensionReceiver = typedRule.receiverExpression,
                allParameters = ArrayList<Any?>().apply {
                    callExp.valueArguments.forEachBetween(
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
    handle<KtSafeQualifiedExpression>(
        condition = {
            val callExp = typedRule.selectorExpression as? KtCallExpression ?: return@handle false
            replacements.getCall(callExp.resolvedCall ?: return@handle false) != null
        },
        priority = 10_001,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val resolvedCall = callExp.resolvedCall!!
            val rule = replacements.getCall(resolvedCall)!!

            val rec: String = if (typedRule.actuallyCouldBeExpression) {
                -"((_it)=>{\n"
                -"if(_it === null) return null;\nreturn "
                "_it"
            } else {
                val n = "temp${uniqueNumber.getAndIncrement()}"
                -"const $n = "
                -typedRule.receiverExpression
                -";\n"
                n
                -"if("
                -n
                -" !== null) "
                n
            }
            emitTemplate(
                requiresWrapping = typedRule.actuallyCouldBeExpression,
                template = rule.template,
                receiver = rec,
                dispatchReceiver = nre.getTsReceiver(),
                extensionReceiver = typedRule.receiverExpression,
                allParameters = ArrayList<Any?>().apply {
                    callExp.valueArguments.forEachBetween(
                        forItem = { add(it) },
                        between = { add(", ") }
                    )
                },
                parameter = resolvedCall.template_parameter,
                typeParameter = resolvedCall.template_typeParameter,
                parameterByIndex = resolvedCall.template_parameterByIndex,
                typeParameterByIndex = resolvedCall.template_typeParameterByIndex
            )
            if (typedRule.actuallyCouldBeExpression) {
                -"\n})("
                -typedRule.receiverExpression
                -')'
            }
        }
    )

    handle<KtCallExpression>(
        condition = {
            replacements.getCall(typedRule.resolvedCall ?: return@handle false) != null
        },
        priority = 10_001,
        action = {
            val nre = typedRule.calleeExpression as KtNameReferenceExpression
            val resolvedCall = typedRule.resolvedCall!!
            val rule = replacements.getCall(resolvedCall)!!

            emitTemplate(
                requiresWrapping = typedRule.actuallyCouldBeExpression,
                template = rule.template,
                receiver = nre.getTsReceiver(),
                dispatchReceiver = nre.getTsReceiver(),
                allParameters = ArrayList<Any?>().apply {
                    typedRule.valueArguments.forEachBetween(
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
        typedRule.on.typeParameters.filter { it.isReified }.forEach {
            if (first) {
                first = false
            } else {
                -", "
            }
            -typedRule.resolvedCall?.typeArguments?.get(it)?.let { CompleteReflectableType(it) }
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
    val resolvedCall: ResolvedCall<out CallableDescriptor>? = null,
    val prependArguments: List<Any> = listOf(),
    val appendArguments: List<Any> = listOf(),
    val orderedArguments: List<Pair<Any, PsiComment?>> = listOf(),
    val namedArguments: List<Pair<KtValueArgument, PsiComment?>> = listOf(),
    val lambdaArgument: KtLambdaArgument? = null
) {

}
