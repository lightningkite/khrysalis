package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.typescript.manifest.declaresPrefix
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
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.classValueType
import org.jetbrains.kotlin.resolve.descriptorUtil.declaresOrInheritsDefaultValue
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter
import java.lang.Exception

//TODO: Local function edgecase - the meaning of 'this' changes

val FunctionDescriptor.tsName: String?
    get() = if (this is ConstructorDescriptor && this.isPrimary == false) {
        this.constructedClass.tsTopLevelName + "." + this.tsName
    } else this.annotations
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
            .joinToString("") { it.capitalize() }.decapitalize() +
                this.name.identifier.capitalize()
    } else if (this.name.isSpecial) {
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
                name = typedRule.resolvedFunction?.tsName ?: typedRule.nameIdentifier!!,
                resolvedFunction = typedRule.resolvedFunction,
                typeParameters = typedRule.typeParameters,
                valueParameters = (typedRule.typeParameters.filter { it.hasModifier(KtTokens.REIFIED_KEYWORD) }
                    .map { listOf(it.name, ": any") })
                    .plus(typedRule.valueParameters),
                returnType = typedRule.typeReference ?: typedRule.bodyExpression?.takeUnless { it is KtBlockExpression }?.resolvedExpressionTypeInfo?.type
                ?: "void",
                body = null
            )
            -mainDecl
            if (typedRule.bodyExpression != null) {
                val tr = typedRule
                val ktClass = typedRule.parentOfType<KtClassBody>()!!.parentOfType<KtClass>()!!
                ktClass.addPostAction {
                    withReceiverScope(tr.resolvedFunction!!.fqNameSafe.asString()) { rName2 ->
                        -"\npublic static "
                        -VirtualFunction(
                            name = tr.resolvedFunction!!.tsDefaultName!!,
                            resolvedFunction = tr.resolvedFunction,
                            typeParameters = ktClass.typeParameters + mainDecl.typeParameters,
                            valueParameters = listOf(rName2) + mainDecl.valueParameters,
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
            if(typedRule.hasModifier(KtTokens.ABSTRACT_KEYWORD)){
                -"abstract "
            }
        } else {
            if (typedRule.isTopLevel() && !typedRule.isPrivate()) {
                -"$declaresPrefix${typedRule.fqName?.asString()}\n"
                -"export "
            }
            -"function "
        }
        fun emit(rName: String? = null) {
            -VirtualFunction(
                name = typedRule.resolvedFunction?.tsName ?: typedRule.nameIdentifier!!,
                resolvedFunction = typedRule.resolvedFunction,
                typeParameters = typedRule.typeParameters,
                valueParameters = (typedRule.receiverTypeReference?.let {
                    listOf(listOf(rName, ": ", it))
                } ?: listOf())
                    .plus(typedRule.typeParameters.filter { it.hasModifier(KtTokens.REIFIED_KEYWORD) }
                        .map { listOf(it.name, ": any") })
                    .plus(typedRule.valueParameters),
                returnType = typedRule.typeReference ?: typedRule.bodyExpression?.resolvedExpressionTypeInfo?.type
                ?: "void",
                body = typedRule.bodyExpression
            )
        }
        typedRule.receiverTypeReference?.let {
            withReceiverScope(typedRule.resolvedFunction!!.fqNameSafe.asString()) { rName ->
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
            ((typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.candidateDescriptor as? FunctionDescriptor)?.extensionReceiverParameter != null
        },
        priority = 1001,
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
            typedRule.actuallyCouldBeExpression
                    && ((typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.candidateDescriptor as? FunctionDescriptor)?.extensionReceiverParameter != null
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
            if (f.constructedClass.let { it.tsTopLevelMessedUp || it.containingDeclaration !is ClassDescriptor }) {
                out.addImport(f.constructedClass, f.constructedClass.tsTopLevelName)
            }
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
        condition = { typedRule.resolvedReferenceTarget is FunctionDescriptor },
        priority = 50,
        action = {
            -((typedRule.resolvedReferenceTarget as FunctionDescriptor).tsName ?: typedRule.text)
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
            -(f.tsName ?: typedRule.operationReference.text)
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
            val f = typedRule.operationReference.resolvedReferenceTarget as? FunctionDescriptor ?: return@handle false
            replacements.getCall(f) != null
        },
        priority = 10_000,
        action = {
            val f = typedRule.operationReference.resolvedReferenceTarget as FunctionDescriptor
            val rule = replacements.getCall(f)!!

            val allParametersByIndex = mapOf<Int, Any>(0 to typedRule.right!!)
            val allParametersByName = mapOf<String, Any>(f.valueParameters.first().name.asString() to typedRule.right!!)

            emitTemplate(
                requiresWrapping = typedRule.actuallyCouldBeExpression,
                template = rule.template,
                receiver = typedRule.left,
                dispatchReceiver = typedRule.operationReference.getTsReceiver() ?: typedRule.left,
                allParameters = typedRule.right,
                parameter = { typedRule.right },
                parameterByIndex = { typedRule.right }
            )
        }
    )

    //Equivalents replacements
    handle<KtDotQualifiedExpression>(
        condition = {
            val desc =
                (((typedRule.selectorExpression as? KtCallExpression)?.calleeExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? FunctionDescriptor)
                    ?: return@handle false
            replacements.getCall(desc, receiverType = typedRule.receiverExpression.resolvedExpressionTypeInfo?.type) != null
        },
        priority = 10_000,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val f = nre.resolvedReferenceTarget as FunctionDescriptor
            val rule = replacements.getCall(f, receiverType = typedRule.receiverExpression.resolvedExpressionTypeInfo?.type)!!

            val allParametersByIndex = HashMap<Int, KtValueArgument>()
            val allParametersByName = HashMap<String, KtValueArgument>()
            callExp.valueArguments.forEachIndexed { index, valueArgument ->
                val argName = valueArgument.getArgumentName()?.text
                if (argName != null) {
                    allParametersByName[argName] = valueArgument
                    val i = f.valueParameters.indexOfFirst { it.name.asString() == argName }
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
                parameter = { allParametersByName[it.name]?.getArgumentExpression() ?: "undefined" },
                typeParameter = { typeParametersByName[it.name] ?: "undefined" },
                parameterByIndex = { allParametersByIndex[it.index]?.getArgumentExpression() ?: "undefined" },
                typeParameterByIndex = { typeParametersByIndex[it.index] ?: "undefined" }
            )
        }
    )
    handle<KtSafeQualifiedExpression>(
        condition = {
            val desc =
                (((typedRule.selectorExpression as? KtCallExpression)?.calleeExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? FunctionDescriptor)
                    ?: return@handle false
            replacements.getCall(desc, receiverType = typedRule.receiverExpression.resolvedExpressionTypeInfo?.type) != null
        },
        priority = 10_001,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val f = nre.resolvedReferenceTarget as FunctionDescriptor
            val rule = replacements.getCall(f, receiverType = typedRule.receiverExpression.resolvedExpressionTypeInfo?.type)!!

            val allParametersByIndex = HashMap<Int, KtValueArgument>()
            val allParametersByName = HashMap<String, KtValueArgument>()
            callExp.valueArguments.forEachIndexed { index, valueArgument ->
                val argName = valueArgument.getArgumentName()?.text
                if (argName != null) {
                    allParametersByName[argName] = valueArgument
                    val i = f.valueParameters.indexOfFirst { it.name.asString() == argName }
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

            val rec: String = if (!typedRule.actuallyCouldBeExpression) {
                val n = "temp${uniqueNumber.getAndIncrement()}"
                -"const $n = "
                -typedRule.receiverExpression
                -";\n"
                n
                -"if("
                -n
                -" !== null) "
                n
            } else {
                -"((_it)=>{\n"
                -"if(_it === null) return null;\nreturn "
                "_it"
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
                parameter = { allParametersByName[it.name]?.getArgumentExpression() ?: "undefined" },
                typeParameter = { typeParametersByName[it.name] ?: "undefined" },
                parameterByIndex = { allParametersByIndex[it.index]?.getArgumentExpression() ?: "undefined" },
                typeParameterByIndex = { typeParametersByIndex[it.index] ?: "undefined" }
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
                val argName = valueArgument.getArgumentName()?.text
                if (argName != null) {
                    allParametersByName[argName] = valueArgument
                    val i = f.valueParameters.indexOfFirst { it.name.asString() == argName }
                    if (i != -1) {
                        allParametersByIndex[i] = valueArgument
                    }
                } else if(index < f.valueParameters.size){
                    allParametersByIndex[index] = valueArgument
                    allParametersByName[f.valueParameters[index].name.asString()] = valueArgument
                }
            }
            val typeParametersByName =
                typedRule.resolvedCall?.typeArguments?.mapKeys { it.key.name.asString() } ?: mapOf()
            val typeParametersByIndex = typedRule.resolvedCall?.typeArguments?.mapKeys { it.key.index } ?: mapOf()

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
                parameter = { allParametersByName[it.name]?.getArgumentExpression() ?: "undefined" },
                typeParameter = { typeParametersByName[it.name] ?: "undefined" },
                parameterByIndex = { allParametersByIndex[it.index]?.getArgumentExpression() ?: "undefined" },
                typeParameterByIndex = { typeParametersByIndex[it.index] ?: "undefined" }
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
            -typedRule.resolvedCall?.typeArguments?.get(it)?.let { CompleteReflectableType(it) }
            if (first) {
                first = false
            } else {
                -", "
            }
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