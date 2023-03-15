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
import com.lightningkite.khrysalis.generic.KotlinTranslator
import com.lightningkite.khrysalis.replacements.Template
import com.lightningkite.khrysalis.replacements.TemplatePart
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.synthetic.hasJavaOriginInHierarchy
import com.lightningkite.khrysalis.analysis.*
import com.lightningkite.khrysalis.util.*
import org.jetbrains.kotlin.builtins.extractParameterNameFromFunctionTypeArgument
import org.jetbrains.kotlin.builtins.functions.FunctionClassDescriptor
import org.jetbrains.kotlin.builtins.functions.FunctionInvokeDescriptor
import org.jetbrains.kotlin.builtins.isFunctionType
import org.jetbrains.kotlin.builtins.isFunctionTypeOrSubtype
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.resolve.calls.components.isVararg
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.scopes.HierarchicalScope
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isAny
import org.jetbrains.kotlin.util.capitalizeDecapitalize.decapitalizeAsciiOnly

private val autogenParamName = Regex("p[0-9]+")
private val ValueParameterDescriptor_useName = HashMap<ValueParameterDescriptor, Boolean>()
private val namelessAnnotation = FqName("com.lightningkite.khrysalis.SwiftNameless")
private fun CallableDescriptor.calculateSwiftParameterNames() {
    val firstRealParamIndex = this.valueParameters.indexOfFirst { !it.name.isSpecial }
    val firstParamUsesUnderscore = this.valueParameters.firstOrNull { !it.name.isSpecial }?.let {
        val stringName = it.name.asString()
        if (stringName matches autogenParamName) return@let true
        if (stringName == it.type.constructor.declarationDescriptor?.name?.asString()?.decapitalizeAsciiOnly() &&
            this.valueParameters.subList(firstRealParamIndex + 1, this.valueParameters.size).none {
                it.name.asString() == it.type.constructor.declarationDescriptor?.name?.asString()
                    ?.decapitalizeAsciiOnly() && !it.declaresDefaultValue()
            }
        ) return@let true
        if (stringName == "value" || stringName == "element") return@let true
        if (stringName.length == 1) return@let true
        return@let false
    }
    if (firstParamUsesUnderscore == true) {
        valueParameters.forEach {
            ValueParameterDescriptor_useName[it] = !it.annotations.hasAnnotation(namelessAnnotation)
        }
        ValueParameterDescriptor_useName[valueParameters[0]] = false
    } else {
        valueParameters.forEach {
            ValueParameterDescriptor_useName[it] = !it.annotations.hasAnnotation(namelessAnnotation)
        }
    }
}

val KtElement.caught: Boolean get() {
    for(it in generateSequence<PsiElement>(this) { it.parent }) {
        if(it is KtTryExpression) return true
        if(it is KtFunction) {
            if(it is KtFunctionLiteral) {
                if(it.parentOfType<KtLambdaExpression>()?.resolvedExpectedExpressionType?.throws == true) return true
            }
            return it.resolvedFunction?.throws == true
        }
    }
    return false
}/*= .takeWhile { it !is KtFunction }.any { it is KtTryExpression }*/

val ValueParameterDescriptor.useName: Boolean
    get() = !this.name.isSpecial && ValueParameterDescriptor_useName[this] ?: run {
        this.containingDeclaration.calculateSwiftParameterNames()
        ValueParameterDescriptor_useName[this]!! && !this.isVararg
    }

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
                .fqNameWithoutTypeArgs
                .split('.')
                .dropWhile { it.firstOrNull()?.isUpperCase() != true }
                .joinToString("") { it.capitalize() }.let { "x$it" } +
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

fun Template.fixTry(forElement: KtElement): Template {
    val caught = forElement.caught

    return when {
        useOptionalThrows -> this.replace("try!", "try?")
        caught -> this.replace("try!", "try")
        else -> this
    }
}

fun SwiftTranslator.registerFunction() {

    handle<VirtualFunction> {
        //TODO: Replace stars with type args
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
        if (typedRule.resolvedFunction?.throws == true) {
            -" throws"
        }
        -" -> "
        -typedRule.returnType
        -' '
        val body = typedRule.body
        when {
            body == null -> {
            }
            body is KtBlockExpression -> {
                -body
            }
            body is KtBinaryExpression
                    && body.operationToken == KtTokens.ELVIS
                    && body.right.let { it is KtThrowExpression } -> {
                -"{ \nguard let result = ("
                -body.left
                -") else {"
                -body.right
                -"}\nreturn result; \n}"
            }
            body !is KtExpression -> {
                -body
            }
            else -> {
                -"{ \nreturn "
                -body
                -"; \n}"
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
            -SwiftExtensionStart(
                typedRule.resolvedFunction!!,
                typedRule.receiverTypeReference,
                typedRule.typeParameterList
            )
            -'\n'
            doSuper()
            -"\n}"
            typeParameterReplacements.set(null)
        }
    )
    handle<KtNamedFunction> {
        val isMember = typedRule.containingClassOrObject != null
        if (isMember) {
            if (typedRule.resolvedFunction?.useOverrideInSwift() == true) {
                -"override "
            }
        }
        if (isMember || (typedRule.isTopLevel && !(typedRule.isExtensionDeclaration() && typedRule.resolvedFunction?.worksAsSwiftConstraint() == true))) {
            -(typedRule.swiftVisibility() ?: "public")
            -' '
        }
        val resolved = typedRule.resolvedFunction
        fun emit(rName: String? = null) {
            val typesUsedInReceiver = if(!isMember)
                typedRule.receiverTypeReference?.walkTopDown()
                    ?.mapNotNull { (it as? KtNameReferenceExpression)?.text }
                    ?.toSet() ?: setOf()
            else setOf()
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
        if ((typedRule.resolvedValueParameter as? ValueParameterDescriptor)?.useName == false) {
            -"_ "
        }
        -typedRule.nameIdentifier
        typedRule.typeReference?.let {
            -": "
            val typeReplacement = it.resolvedType?.let { replacements.getType(it) }
            if (it.resolvedType?.isMarkedNullable != true && (it.resolvedType?.constructor?.declarationDescriptor is FunctionClassDescriptor || typeReplacement?.isFunctionType == true)) {
                -"@escaping "
            }
            if (typedRule.resolvedValueParameter?.annotations?.let {
                    it.hasAnnotation(FqName("com.lightningkite.khrysalis.Modifies")) || it.hasAnnotation(FqName("com.lightningkite.khrysalis.modifies"))
                } == true) {
                -"inout "
            }
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
            if ((typedRule.resolvedValueParameter as? ValueParameterDescriptor)?.useName == false) {
                -"_ "
            }
            -typedRule.nameIdentifier
            -": "
            -typedRule.typeReference
            -"..."
        }
    )

    fun KotlinTranslator<SwiftFileEmitter>.ContextByType<*>.maybeWrapCall(
        call: ResolvedCall<out CallableDescriptor>,
        action: () -> Unit
    ) {
        val explicitTypeArgs = call.call.typeArgumentList != null
        val needsTry = call.candidateDescriptor.throws == true
        val needsExplicitReturn =
            explicitTypeArgs && call.resultingDescriptor.original.returnType?.contains { it.isTypeParameter() } == true
        val isExpression = (call.call.callElement as? KtExpression)?.actuallyCouldBeExpression == true

        if (needsExplicitReturn) {
            -'('
        }
        if (needsTry) {
            val caught = call.call.callElement.caught
            if (isExpression) {
                -"("
            }
            if (useOptionalThrows) {
                -"try? "
            } else if (caught) {
                -"try "
            } else {
                -"try! "
            }
        }
        action()
        if (needsTry && isExpression) {
            -")"
        }
        if (needsExplicitReturn) {
            -" as "
            -call.resultingDescriptor.returnType
            -')'
        }
    }

    //lambda call
    handle<KtCallExpression>(
        condition = {
            typedRule.calleeExpression
                ?.let { it as? KtNameReferenceExpression}
                ?.resolvedReferenceTarget
                ?.let { it as? ValueDescriptor }
                ?.type?.throws == true
        },
        priority = 20000,
        action = {
            val isExpression = typedRule.actuallyCouldBeExpression
            val caught = typedRule.caught
            if (isExpression) {
                -"("
            }
            if (useOptionalThrows) {
                -"try? "
            } else if (caught) {
                -"try "
            } else {
                -"try! "
            }
            doSuper()
            if (isExpression) {
                -")"
            }
        }
    )
    handle<KtDotQualifiedExpression>(
        condition = {
            (typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.candidateDescriptor is FunctionInvokeDescriptor
        },
        priority = 2000,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val f = callExp.resolvedCall!!.candidateDescriptor as FunctionInvokeDescriptor
            val needsTry = typedRule.receiverExpression.resolvedExpressionTypeInfo?.type?.throws == true
            val isExpression = typedRule.actuallyCouldBeExpression

            if (needsTry) {
                val caught = typedRule.caught
                if (isExpression) {
                    -"("
                }
                if (useOptionalThrows) {
                    -"try? "
                } else if (caught) {
                    -"try "
                } else {
                    -"try! "
                }
            }
            if (nre.text == "invoke") {
                -typedRule.receiverExpression
            } else {
                -typedRule.receiverExpression
                -'.'
                -nre
            }
            -ArgumentsList(
                on = f,
                resolvedCall = callExp.resolvedCall!!
            )
            if (needsTry && isExpression) {
                -")"
            }
        }
    )
    handle<KtSafeQualifiedExpression>(
        condition = {
            (typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.candidateDescriptor is FunctionInvokeDescriptor
        },
        priority = 2000,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val f = callExp.resolvedCall!!.candidateDescriptor as FunctionDescriptor
            val needsTry = typedRule.receiverExpression.resolvedExpressionTypeInfo?.type?.throws == true
            val isExpression = typedRule.actuallyCouldBeExpression

            if (needsTry) {
                val caught = typedRule.caught
                if (isExpression) {
                    -"("
                }
                if (useOptionalThrows) {
                    -"try? "
                } else if (caught) {
                    -"try "
                } else {
                    -"try! "
                }
            }

            if (nre.text == "invoke") {
                -typedRule.receiverExpression
            } else {
                -typedRule.receiverExpression
                -'.'
                -nre
            }
            -'?'
            -ArgumentsList(
                on = f,
                resolvedCall = callExp.resolvedCall!!
            )
            if (needsTry && isExpression) {
                -")"
            }
        }
    )

    //Normal calls
    handle<KtDotQualifiedExpression>(
        condition = {
            val desc =
                (typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.candidateDescriptor as? FunctionDescriptor
                    ?: return@handle false
            desc.swiftNameOverridden != null && !(desc is ConstructorDescriptor && desc.constructedClass.swiftTopLevelMessedUp)
        },
        priority = 1000,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val f = callExp.resolvedCall!!.candidateDescriptor as FunctionDescriptor
            val desc =
                (typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.candidateDescriptor as? FunctionDescriptor

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
            nullWrapAction(this@registerFunction, typedRule) { rec, mode ->
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
            if((call.calleeExpression as? KtReferenceExpression)?.resolvedReferenceTarget is ValueDescriptor) {
                -".invoke"
            }
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

        nullWrapAction(this@registerFunction, typedRule) { rec, mode ->
            maybeWrapCall(resolvedCall) {
                -typedRule.receiverExpression
                if (mode == AccessMode.QUEST_DOT) {
                    -'?'
                }
                insertNewlineBeforeAccess()
                -'.'
                -call.calleeExpression
                if((call.calleeExpression as? KtReferenceExpression)?.resolvedReferenceTarget is ValueDescriptor) {
                    -".invoke"
                }
                -ArgumentsList(
                    on = resolvedCall.resultingDescriptor as FunctionDescriptor,
                    resolvedCall = resolvedCall,
                    prependArguments = listOf()
                )
            }
        }
    }

    handle<KtCallExpression>(
        condition = {
            val target = (typedRule.calleeExpression as? KtReferenceExpression)?.resolvedReferenceTarget
            target is ValueDescriptor && !target.type.isFunctionTypeOrSubtype
        },
        priority = 2
    ) {
        -typedRule.calleeExpression
        -".invoke"
        -ArgumentsList(
            on = typedRule.resolvedCall!!.candidateDescriptor as FunctionDescriptor,
            resolvedCall = typedRule.resolvedCall!!
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
        condition = { typedRule.operationReference.getIdentifier() != null && typedRule.operationReference.resolvedReferenceTarget is FunctionDescriptor },
        priority = 1_000,
        action = {
            val f = typedRule.operationReference.resolvedReferenceTarget as FunctionDescriptor
            val doubleReceiver = f.dispatchReceiverParameter != null && f.extensionReceiverParameter != null
            val swiftNameOverridden = f.swiftNameOverridden != null
            maybeWrapCall(typedRule.resolvedCall!!) {
                if (doubleReceiver) {
                    -typedRule.getTsReceiver()
                    -"."
                } else if (!swiftNameOverridden && (f.dispatchReceiverParameter != null || f.extensionReceiverParameter != null)) {
                    -typedRule.left
                    -"."
                }
                -(f.swiftNameOverridden ?: typedRule.operationReference.text)
                -ArgumentsList(
                    on = f,
                    resolvedCall = typedRule.resolvedCall!!,
                    prependArguments = if (doubleReceiver || swiftNameOverridden) listOf(typedRule.left!!) else listOf()
                )
            }
        }
    )

    //Equivalents replacements
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
                template = rule.template.fixTry(typedRule),
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
            replacements.getCall(callExp.resolvedCall ?: return@handle false) != null
        },
        priority = 10_000,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val resolvedCall = callExp.resolvedCall!!
            val rule = replacements.getCall(resolvedCall)!!
            val templateIsThisDot =
                rule.template.parts.getOrNull(0) is TemplatePart.Receiver && rule.template.parts.getOrNull(1)
                    .let { it is TemplatePart.Text && it.string.startsWith('.') }

            emitTemplate(
                requiresWrapping = typedRule.actuallyCouldBeExpression,
                template = rule.template.fixTry(typedRule),
                receiver = if (typedRule.hasNewlineBeforeAccess && templateIsThisDot) listOf(
                    typedRule.receiverExpression,
                    "\n"
                ) else typedRule.receiverExpression,
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

            nullWrapAction(this@registerFunction, typedRule) { rec, mode ->
                emitTemplate(
                    requiresWrapping = typedRule.actuallyCouldBeExpression,
                    template = (if (mode == AccessMode.QUEST_DOT)
                        rule.template.copy(parts = rule.template.parts.toMutableList().apply {
                            this[1] =
                                (this[1] as TemplatePart.Text).let { it.copy("?" + (if (typedRule.hasNewlineBeforeAccess) "\n" else "") + it.string) }
                        })
                    else rule.template).fixTry(typedRule),
                    receiver = if (typedRule.hasNewlineBeforeAccess && mode == AccessMode.PLAIN_DOT) listOf(
                        rec,
                        "\n"
                    ) else rec,
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
            replacements.getCall(typedRule.resolvedCall ?: return@handle false) != null
        },
        priority = 10_001,
        action = {
            val nre = typedRule.calleeExpression as KtNameReferenceExpression
            val resolvedCall = typedRule.resolvedCall!!
            val rule = replacements.getCall(resolvedCall)!!

            emitTemplate(
                requiresWrapping = typedRule.actuallyCouldBeExpression,
                template = rule.template.fixTry(typedRule),
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
        if (typedRule.on is ConstructorDescriptor
            && (typedRule.resolvedCall.call.callElement.containingClass()?.resolvedClass == typedRule.on.containingDeclaration
                    || typedRule.resolvedCall.call.callElement.parentOfType<KtFunction>()?.resolvedFunction?.extensionReceiverParameter?.type?.constructor?.declarationDescriptor == typedRule.on.containingDeclaration)
        ) {
            typedRule.resolvedCall.typeArguments
                .entries
                .sortedBy { it.key.index }
                .map { it.value }
                .takeUnless { it.isEmpty() }
                ?.let {
                    -"<"
                    it.forEachBetween(
                        forItem = {
                            -it
                        },
                        between = { -", " }
                    )
                    -">"
                }
        }
        val lambdaArgument = typedRule.on.valueParameters.lastOrNull()
            ?.let { typedRule.resolvedCall.valueArguments[it] }
            ?.takeIf { it.arguments.size == 1 }
            ?.takeIf { it.arguments[0].getArgumentExpression() is KtLambdaExpression }
            ?.takeIf {
                typedRule.on.valueParameters.asReversed().drop(1).firstOrNull()?.type?.isFunctionTypeOrSubtype != true
            }
        val isFunctionInvoke = typedRule.on is FunctionInvokeDescriptor
        val parenArguments = typedRule.prependArguments.plus(typedRule.resolvedCall.valueArguments.entries
            .filter { it.value.arguments.isNotEmpty() }
            .filter { it.value != lambdaArgument }
            .sortedBy { it.key.index }
            .map { entry ->
                val out = ArrayList<Any?>()
                if (!isFunctionInvoke && !typedRule.on.hasJavaOriginInHierarchy() && !entry.key.isVararg) {
                    if (entry.key.useName) {
                        out += entry.key.name.asString().safeSwiftIdentifier()
                        out += ": "
                    }
                }
                typedRule.replacements[entry.key]?.let {
                    out += it
                } ?: entry.value.arguments.forEachBetween(
                    forItem = {
                        if (entry.key.annotations.hasAnnotation(FqName("com.lightningkite.khrysalis.Modifies")) || entry.key.annotations.hasAnnotation(
                                FqName("com.lightningkite.khrysalis.modifies")
                            )
                        ) {
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
        lambdaArgument
            ?.let {
                -' '
                -it.arguments.single().getArgumentExpression()
            }
        if (lambdaArgument == null && parenArguments == null) {
            -"()"
        }
    }
}

fun FunctionDescriptor.useOverrideInSwift(): Boolean = overriddenDescriptors
    .filter { (it.containingDeclaration as? ClassDescriptor)?.defaultType?.isAny() != true }
    .any { (it.containingDeclaration as? ClassDescriptor)?.kind != ClassKind.INTERFACE }

data class ArgumentsList(
    val on: FunctionDescriptor,
    val resolvedCall: ResolvedCall<out CallableDescriptor>,
    val prependArguments: List<Any> = listOf(),
    val replacements: Map<ValueParameterDescriptor, Any?> = mapOf()
) {

}
