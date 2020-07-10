package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.util.forEachBetween
import com.lightningkite.khrysalis.util.walkTopDown
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.psi2ir.findFirstFunction
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall

val FunctionDescriptor.swiftNameOverridden: String?
    get() = this.annotations
        .find { it.fqName?.asString()?.substringAfterLast('.') == "SwiftName" }
        ?.allValueArguments
        ?.entries
        ?.firstOrNull()
        ?.value
        ?.value
        ?.toString()?.safeSwiftIdentifier() ?: when {
        this.worksAsSwiftConstraint() && this.containingDeclaration !is ClassDescriptor -> null
        extensionReceiverParameter != null -> {
            extensionReceiverParameter!!
                .value
                .type
                .getJetTypeFqName(false)
                .split('.')
                .joinToString("") { it.capitalize() }.decapitalize() +
                    this.name.identifier.capitalize()
        }
        else -> this.overriddenDescriptors.asSequence().mapNotNull { it.swiftNameOverridden }.firstOrNull()
    }

val FunctionDescriptor.swiftName: String?
    get() = swiftNameOverridden ?: if (this.name.isSpecial) {
        null
    } else {
        this.name.identifier
    }

data class VirtualFunction(
    val name: Any,
    val resolvedFunction: FunctionDescriptor? = null,
    val typeParameters: List<Any>,
    val valueParameters: List<Any>,
    val returnType: Any,
    val body: Any?
)

fun SwiftTranslator.registerFunction() {
    handle<VirtualFunction> {
        -"func "
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
        -" -> "
        -typedRule.returnType
        -' '
        val body = typedRule.body
        when (body) {
            null -> {
            }
            is KtBlockExpression -> {
                -body
            }
            is KtExpression -> {
                -"{ return "
                -body
                -" }"
            }
            else -> {
                -body
            }
        }

    }
    handle<KtNamedFunction>(
        condition = { typedRule.parentOfType<KtClassBody>()?.parentOfType<KtClass>()?.isInterface() == true },
        priority = 100,
        action = {
            if (typedRule.receiverTypeReference != null) throw IllegalArgumentException("Receivers on interface methods aren't supported yet.")
            val mainDecl = VirtualFunction(
                name = typedRule.resolvedFunction?.swiftNameOverridden ?: typedRule.nameIdentifier!!,
                resolvedFunction = typedRule.resolvedFunction,
                typeParameters = typedRule.typeParameters,
                valueParameters = typedRule.valueParameters,
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
                    -"\n"
                    -mainDecl.copy(
                        body = tr.bodyExpression
                    )
                }
            }
        }
    )
    handle<KtNamedFunction>(
        condition = {
            typedRule.receiverTypeReference != null
                && typedRule.resolvedFunction?.worksAsSwiftConstraint() == true
                    && typedRule.containingClassOrObject == null
        },
        priority = 10,
        action = {
            val isMember = typedRule.containingClassOrObject != null
            if (isMember || typedRule.isTopLevel) {
                -(typedRule.visibilityModifier() ?: "public")
                -" "
            }
            -SwiftExtensionStart(typedRule.resolvedFunction!!)
            -'\n'
            doSuper()
            -"\n}"
        }
    )
    handle<KtNamedFunction> {
        val isMember = typedRule.containingClassOrObject != null
        if (isMember) {
            if (typedRule.resolvedFunction?.overriddenDescriptors
                    ?.any { (it.containingDeclaration as? ClassDescriptor)?.kind != ClassKind.INTERFACE } == true
            ) {
                -"override "
            }
        }
        if (isMember || (typedRule.isTopLevel && !typedRule.isExtensionDeclaration())) {
            -(typedRule.visibilityModifier() ?: "public")
            -' '
        }
        val resolved = typedRule.resolvedFunction
        fun emit(rName: String? = null) {
            val typesUsedInReceiver = typedRule.receiverTypeReference?.walkTopDown()
                ?.mapNotNull { (it as? KtNameReferenceExpression)?.text }
                ?.toSet() ?: setOf()
            -VirtualFunction(
                name = resolved?.swiftNameOverridden ?: typedRule.nameIdentifier!!,
                resolvedFunction = resolved,
                typeParameters = typedRule.typeParameters.filter { it.name !in typesUsedInReceiver },
                valueParameters = (rName?.let { rName ->
                    listOf(listOf("_ ", rName, ": ", resolved!!.extensionReceiverParameter!!.type))
                } ?: listOf())
                    .plus(typedRule.valueParameters),
                returnType = typedRule.typeReference
                    ?: typedRule.bodyExpression?.takeUnless { it is KtBlockExpression }?.resolvedExpressionTypeInfo?.type
                    ?: "Void",
                body = typedRule.bodyExpression
            )
        }
        if (typedRule.receiverTypeReference != null &&
            (typedRule.resolvedFunction?.worksAsSwiftConstraint() != true ||
            typedRule.containingClassOrObject != null)
        ) {
            withReceiverScope(resolved!!) { rName ->
                emit(rName)
            }
        } else {
            emit(null)
        }
    }

    handle<KtParameter> {
        -typedRule.nameIdentifier
        typedRule.typeReference?.let {
            -": "
            partOfParameter = true
            -it
            partOfParameter = false
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
            -typedRule.nameIdentifier
            -": "
            partOfParameter = true
            -typedRule.typeReference
            partOfParameter = false
            -"..."
        }
    )

    //Normal calls
    handle<KtDotQualifiedExpression>(
        condition = {
            ((typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.candidateDescriptor as? FunctionDescriptor)?.swiftNameOverridden != null
        },
        priority = 1000,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val f = callExp.resolvedCall!!.candidateDescriptor as FunctionDescriptor

            -nre
            -ArgumentsList(
                on = f,
                resolvedCall = callExp.resolvedCall!!,
                prependArguments = listOf(typedRule.receiverExpression)
            )
        }
    )

    handle<KtSafeQualifiedExpression>(
        condition = {
            ((typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.candidateDescriptor as? FunctionDescriptor)?.swiftNameOverridden != null
        },
        priority = 1002,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val f = callExp.resolvedCall!!.candidateDescriptor as FunctionDescriptor

            if (typedRule.actuallyCouldBeExpression) {
                -"run {"
            }
            val rec = "temp${uniqueNumber.getAndIncrement()}"
            -"if let $rec = ("
            -typedRule.receiverExpression
            -") {\n"
            -nre
            -ArgumentsList(
                on = f,
                resolvedCall = callExp.resolvedCall!!,
                prependArguments = listOf(rec)
            )
            -"\n}"
            if (typedRule.actuallyCouldBeExpression) {
                -"}"
            }
        }
    )

    handle<KtCallExpression>(
        condition = { (typedRule.calleeExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget is FunctionDescriptor },
        priority = 1
    ) {
        val nre = typedRule.calleeExpression as KtNameReferenceExpression
        val f = nre.resolvedReferenceTarget as FunctionDescriptor

        -nre
        //-typedRule.typeArgumentList
        //The tricky part: these *must* be implied
        -ArgumentsList(
            on = f,
            resolvedCall = typedRule.resolvedCall!!,
            prependArguments = listOf()
        )
    }

    handle<KtNameReferenceExpression>(
        condition = { (typedRule.resolvedReferenceTarget as? FunctionDescriptor)?.swiftNameOverridden != null },
        priority = 50,
        action = {
            -(typedRule.resolvedReferenceTarget as FunctionDescriptor).swiftNameOverridden!!
        }
    )

    //infix
    handle<KtBinaryExpression>(
        condition = { typedRule.operationReference.getIdentifier() != null },
        priority = 1_000,
        action = {
            val f = typedRule.operationReference.resolvedReferenceTarget as FunctionDescriptor
            val doubleReceiver = f.dispatchReceiverParameter != null && f.extensionReceiverParameter != null
            if (doubleReceiver) {
                -typedRule.getTsReceiver()
                -"."
            } else if (f.dispatchReceiverParameter != null) {
                -typedRule.left
                -"."
            }
            -(f.swiftNameOverridden ?: typedRule.operationReference.text)
            -ArgumentsList(
                on = f,
                resolvedCall = typedRule.resolvedCall!!,
                prependArguments = if (f.extensionReceiverParameter != null) listOf(typedRule.left!!) else listOf()
            )
        }
    )

    //Equivalents replacements
    handle<KtBinaryExpression>(
        condition = {
            if (typedRule.operationReference.getIdentifier() == null) return@handle false
            replacements.getCall(this@registerFunction, typedRule.resolvedCall ?: return@handle false) != null
        },
        priority = 10_000,
        action = {
            val resolvedCall = typedRule.resolvedCall!!
            val rule = replacements.getCall(this@registerFunction, resolvedCall)!!

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
    handle<KtDotQualifiedExpression>(
        condition = {
            val callExp = typedRule.selectorExpression as? KtCallExpression ?: return@handle false
            replacements.getCall(this@registerFunction, callExp.resolvedCall ?: return@handle false) != null
        },
        priority = 10_000,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val resolvedCall = callExp.resolvedCall!!
            val rule = replacements.getCall(this@registerFunction, resolvedCall)!!

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
            replacements.getCall(this@registerFunction, callExp.resolvedCall ?: return@handle false) != null
        },
        priority = 10_001,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val resolvedCall = callExp.resolvedCall!!
            val rule = replacements.getCall(this@registerFunction, resolvedCall)!!

            if (typedRule.actuallyCouldBeExpression) {
                -"run {"
            }
            val rec = "temp${uniqueNumber.getAndIncrement()}"
            -"if let $rec = ("
            -typedRule.receiverExpression
            -") {\n"
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
            -"\n}"
            if (typedRule.actuallyCouldBeExpression) {
                -"}"
            }
        }
    )

    handle<KtCallExpression>(
        condition = {
            replacements.getCall(this@registerFunction, typedRule.resolvedCall ?: return@handle false) != null
        },
        priority = 10_001,
        action = {
            val nre = typedRule.calleeExpression as KtNameReferenceExpression
            val resolvedCall = typedRule.resolvedCall!!
            val rule = replacements.getCall(this@registerFunction, resolvedCall)!!

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

    //Regular calls
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
        typedRule.resolvedCall.valueArguments.entries
            .filter { it.value.arguments.isNotEmpty() }
            .sortedBy { it.key.index }
            .forEach {
                if (first) {
                    first = false
                } else {
                    -", "
                }
                it.key.name.takeUnless { it.isSpecial }?.let {
                    -it.asString()
                    -": "
                }
                it.value.arguments.forEachBetween(
                    forItem = { -it.getArgumentExpression() },
                    between = { -", " }
                )
            }
        -')'
    }
}

data class ArgumentsList(
    val on: FunctionDescriptor,
    val resolvedCall: ResolvedCall<out CallableDescriptor>,
    val prependArguments: List<Any> = listOf()
) {

}
