package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.util.forEachBetween
import com.lightningkite.khrysalis.util.walkTopDown
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.types.typeUtil.contains
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter
import com.lightningkite.khrysalis.generic.PartialTranslatorByType
import com.lightningkite.khrysalis.swift.replacements.Template
import com.lightningkite.khrysalis.swift.replacements.TemplatePart
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.synthetic.hasJavaOriginInHierarchy

val noArgNames = setOf<String>(/*"value", "other"*/)

val FunctionDescriptor.swiftNameOverridden: String?
    get() = this.annotations
        .find { it.fqName?.asString()?.substringAfterLast('.') == "SwiftName" }
        ?.allValueArguments
        ?.entries
        ?.firstOrNull()
        ?.value
        ?.value
        ?.toString()?.safeSwiftIdentifier() ?: when {
        this is ConstructorDescriptor && this.constructedClass.swiftTopLevelMessedUp -> this.constructedClass.swiftTopLevelName
        this.worksAsSwiftConstraint() && this.containingDeclaration !is ClassDescriptor -> null
        extensionReceiverParameter != null -> {
            extensionReceiverParameter!!
                .value
                .type
                .getJetTypeFqName(false)
                .split('.')
                .dropWhile { it.firstOrNull()?.isUpperCase() != true }
                .joinToString("") { it.capitalize() }.let{ "x$it" } +
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
        if(typedRule.resolvedFunction?.annotations?.findAnnotation(FqName("kotlin.jvm.Throws")) != null) {
            -" throws"
        }
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
        condition = {
            typedRule.let { it.parent as? KtClassBody }?.let { it.parent as? KtClass }?.isInterface() == true
        },
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
                    ?: "Void",
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
                -(typedRule.swiftVisibility() ?: "public")
                -" "
            }
            -SwiftExtensionStart(typedRule.resolvedFunction!!, typedRule.receiverTypeReference, typedRule.typeParameterList)
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
        if (isMember || (typedRule.isTopLevel && !(typedRule.isExtensionDeclaration() && typedRule.resolvedFunction?.worksAsSwiftConstraint() == true))) {
            -(typedRule.swiftVisibility() ?: "public")
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
                typeParameters = typedRule.typeParameters.filter { it.name !in typesUsedInReceiver || resolved?.worksAsSwiftConstraint() == false },
                valueParameters = (rName?.let { rName ->
                    listOf(listOf("_ ", rName, ": ", resolved!!.extensionReceiverParameter!!.type))
                } ?: listOf())
                    .plus(typedRule.valueParameters),
                returnType = typedRule.typeReference
                    ?: typedRule.bodyExpression?.takeUnless { it is KtBlockExpression }?.resolvedExpressionTypeInfo?.type
                    ?: "Void",
                body = typedRule.bodyExpression ?: "{ TODO() }"
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
        if (typedRule.name in noArgNames) {
            -"_ "
        }
        -typedRule.nameIdentifier
        typedRule.typeReference?.let {
            -": "
            if (typedRule.resolvedValueParameter?.annotations?.let {
                    it.hasAnnotation(FqName("com.lightningkite.butterfly.Modifies")) || it.hasAnnotation(FqName("com.lightningkite.butterfly.modifies"))
                } == true) {
                -"inout "
            }
            writingParameter++
            -it
            writingParameter--
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
            writingParameter++
            -typedRule.typeReference
            writingParameter--
            -"..."
        }
    )

    fun PartialTranslatorByType<SwiftFileEmitter, Unit, Any>.ContextByType<*>.maybeWrapCall(
        call: ResolvedCall<out CallableDescriptor>,
        action: () -> Unit
    ) {
        val explicitTypeArgs = call.call.typeArgumentList != null
        val needsTry = call.candidateDescriptor.annotations.hasAnnotation(FqName("kotlin.jvm.Throws"))
        val needsExplicitReturn = explicitTypeArgs && call.resultingDescriptor.original.returnType?.contains { it.isTypeParameter() } == true
        val isExpression = (call.call.callElement as? KtExpression)?.actuallyCouldBeExpression == true

        if (needsExplicitReturn) {
            -'('
        }
        if(needsTry){
            val caught = run {
                var base: PsiElement? = call.call.callElement
                while(base != null) {
                    if(base is KtTryExpression)
                        return@run true
                    base = base.parent
                }
                return@run false
            }
            if(isExpression) {
                -"("
            }
            if(useOptionalThrows){
                -"try? "
            } else if(caught) {
                -"try "
            } else {
                -"try! "
            }
        }
        action()
        if(needsTry && isExpression){
            -")"
        }
        if (needsExplicitReturn) {
            -" as "
            -call.resultingDescriptor.returnType
            -')'
        }
    }

    //Normal calls
    handle<KtDotQualifiedExpression>(
        condition = {
            val desc = (typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.candidateDescriptor as? FunctionDescriptor ?: return@handle false
            desc.swiftNameOverridden != null && !(desc is ConstructorDescriptor && desc.constructedClass.swiftTopLevelMessedUp)
        },
        priority = 1000,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val f = callExp.resolvedCall!!.candidateDescriptor as FunctionDescriptor

            maybeWrapCall(callExp.resolvedCall!!) {
                -nre
                -ArgumentsList(
                    on = f,
                    resolvedCall = callExp.resolvedCall!!,
                    prependArguments = listOf(typedRule.receiverExpression)
                )
            }
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
            -"/*Null-wrap call*/"
            nullWrapAction(this@registerFunction, typedRule){ rec, mode ->
                maybeWrapCall(callExp.resolvedCall!!) {
                    -nre
                    -ArgumentsList(
                        on = f,
                        resolvedCall = callExp.resolvedCall!!,
                        prependArguments = listOf(rec)
                    )
                }
            }
        }
    )

    handle<KtCallExpression>(
        condition = { (typedRule.calleeExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget is FunctionDescriptor },
        priority = 1
    ) {
        val nre = typedRule.calleeExpression as KtNameReferenceExpression
        val f = nre.resolvedReferenceTarget as FunctionDescriptor

        maybeWrapCall(typedRule.resolvedCall!!) {
            -nre
            -ArgumentsList(
                on = f,
                resolvedCall = typedRule.resolvedCall!!,
                prependArguments = listOf()
            )
        }
    }

    handle<KtDotQualifiedExpression>(
        condition = { (typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.resultingDescriptor is FunctionDescriptor },
        priority = 1
    ) {
        val call = typedRule.selectorExpression as KtCallExpression
        val resolvedCall = call.resolvedCall!!

        maybeWrapCall(resolvedCall) {
            -typedRule.receiverExpression
            insertNewlineBeforeAccess()
            -'.'
            -call.calleeExpression
            -ArgumentsList(
                on = resolvedCall.resultingDescriptor as FunctionDescriptor,
                resolvedCall = resolvedCall,
                prependArguments = listOf()
            )
        }
    }

    handle<KtSafeQualifiedExpression>(
        condition = { (typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.resultingDescriptor is FunctionDescriptor },
        priority = 1
    ) {
        val call = typedRule.selectorExpression as KtCallExpression
        val resolvedCall = call.resolvedCall!!

        nullWrapAction(this@registerFunction, typedRule){ rec, mode ->
            maybeWrapCall(resolvedCall) {
                -typedRule.receiverExpression
                if(mode == AccessMode.QUEST_DOT) {
                    -'?'
                }
                insertNewlineBeforeAccess()
                -'.'
                -call.calleeExpression
                -ArgumentsList(
                    on = resolvedCall.resultingDescriptor as FunctionDescriptor,
                    resolvedCall = resolvedCall,
                    prependArguments = listOf()
                )
            }
        }
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
            maybeWrapCall(typedRule.resolvedCall!!) {
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
            val templateIsThisDot = rule.template.parts.getOrNull(0) is TemplatePart.Receiver && rule.template.parts.getOrNull(1).let { it is TemplatePart.Text && it.string.startsWith('.') }

            emitTemplate(
                requiresWrapping = typedRule.actuallyCouldBeExpression,
                template = rule.template,
                receiver = if(hasNewlineBeforeAccess(typedRule) && templateIsThisDot) listOf(typedRule.receiverExpression, "\n") else typedRule.receiverExpression,
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

            nullWrapAction(this@registerFunction, typedRule){ rec, mode ->
                emitTemplate(
                    requiresWrapping = typedRule.actuallyCouldBeExpression,
                    template = if(mode == AccessMode.QUEST_DOT)
                        Template(parts = rule.template.parts.toMutableList().apply {
                            this[1] = (this[1] as TemplatePart.Text).let { it.copy("?" + (if(hasNewlineBeforeAccess(typedRule)) "\n" else "") + it.string) }
                        })
                    else rule.template,
                    receiver = if(hasNewlineBeforeAccess(typedRule) && mode == AccessMode.PLAIN_DOT) listOf(rec, "\n") else rec,
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
        val explicitTypeArgs = typedRule.resolvedCall.call.typeArgumentList != null
        val parenArguments = typedRule.prependArguments.plus(typedRule.resolvedCall.valueArguments.entries
            .filter { it.value.arguments.isNotEmpty() && it.value.arguments.none { it.isExternal() && it is LambdaArgument } }
            .sortedBy { it.key.index }
            .map { entry ->
                val out = ArrayList<Any?>()
                if (!typedRule.on.hasJavaOriginInHierarchy()) {
                    entry.key.name.takeUnless { it.isSpecial || it.asString().let { it in noArgNames || (it.startsWith('p') && it.drop(1).all { it.isDigit() } ) } }?.let {
                        out += it.asString().safeSwiftIdentifier()
                        out += ": "
                    }
                }
                typedRule.replacements[entry.key]?.let {
                    out += it
                } ?: entry.value.arguments.forEachBetween(
                    forItem = {
                        if (entry.key.annotations.hasAnnotation(FqName("com.lightningkite.butterfly.Modifies")) || entry.key.annotations.hasAnnotation(FqName("com.lightningkite.butterfly.modifies"))) {
                            out += "&"
                        }
                        out += it.getArgumentExpression()
                        if (explicitTypeArgs && entry.key.original.type.contains { it.isTypeParameter() }) {
                            out += " as "
                            out += entry.key.type
                        }
                    },
                    between = { out += ", " }
                )
                out
            })
            .takeUnless { it.isEmpty() }

        parenArguments?.let {
            -'('
            it.forEachBetween(
                forItem = { -it },
                between = { -", " }
            )
            -')'
        }
        val lambdaArg = typedRule.resolvedCall.valueArguments.entries
            .firstOrNull { it.value.arguments.isNotEmpty() && it.value.arguments.any { it.isExternal() && it is LambdaArgument } }
        lambdaArg
            ?.let {
                -it.value.arguments[0].getArgumentExpression()
            }
        if(lambdaArg == null && parenArguments == null){
            -"()"
        }
    }
}

data class ArgumentsList(
    val on: FunctionDescriptor,
    val resolvedCall: ResolvedCall<out CallableDescriptor>,
    val prependArguments: List<Any> = listOf(),
    val replacements: Map<ValueParameterDescriptor, Any?> = mapOf()
) {

}
