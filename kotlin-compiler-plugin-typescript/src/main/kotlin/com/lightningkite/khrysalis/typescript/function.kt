package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.typescript.manifest.declaresPrefix
import com.lightningkite.khrysalis.util.forEachBetween
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import com.lightningkite.khrysalis.util.fqNamesToCheck
import com.lightningkite.khrysalis.util.simpleFqName
import org.jetbrains.kotlin.builtins.functions.FunctionInvokeDescriptor
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.ir.expressions.typeParametersCount
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.resolve.calls.components.hasDefaultValue
import org.jetbrains.kotlin.resolve.calls.components.isVararg
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.source.KotlinSourceElement
import org.jetbrains.kotlin.types.typeUtil.makeNullable
import org.jetbrains.kotlin.types.typeUtil.nullability

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
            .fqNameWithoutTypeArgs
            .split('.')
            .dropWhile { it.firstOrNull()?.isUpperCase() != true }
            .joinToString("") { it.capitalize() }.let{ "x$it" } +
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
    val body: KtExpression?,
    val arrowStyle: Boolean = false
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
                -"{ \nreturn "
                -body
                -"; \n}"
            }
        }

    }
    handle<VirtualFunction>(condition = { typedRule.arrowStyle }, priority = 1) {
        -"const "
        -typedRule.name
        -" = "
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
        -" => "
        val body = typedRule.body
        when (body) {
            null -> {
            }
            is KtBlockExpression -> {
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
                name = typedRule.resolvedFunction?.tsNameOverridden ?: typedRule.nameIdentifier!!,
                resolvedFunction = typedRule.resolvedFunction,
                typeParameters = typedRule.typeParameters,
                valueParameters = (typedRule.typeParameters.filter { it.hasModifier(KtTokens.REIFIED_KEYWORD) }
                    .map { listOf(it.name, ": Array<any>") })
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
            if(typedRule.isTopLevel() ){
                -"function "
            }
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
                        .map { listOf(it.name, ": Array<any>") })
                    .plus(typedRule.valueParameters),
                returnType = typedRule.typeReference
                    ?: typedRule.bodyExpression?.takeUnless { it is KtBlockExpression }?.resolvedExpressionTypeInfo?.type
                    ?: "void",
                body = typedRule.bodyExpression,
                arrowStyle = !(typedRule.isTopLevel || isMember)
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
        val f = ((typedRule.parent as? KtParameterList)?.parent as? KtFunction)?.resolvedFunction
        if(f != null && f.modality == Modality.ABSTRACT) {
            //suppress defaults!
        } else {
            typedRule.defaultValue?.let {
                -" = "
                -it
            } ?: run {
                if(f != null) {
                    f.overriddenDescriptors
                        .asSequence()
                        .filter {
                            it.valueParameters.find { it.name.asString() == typedRule.name }?.hasDefaultValue() == true
                        }
                        .map { it.original }
                        .mapNotNull { it.source as? KotlinSourceElement }
                        .mapNotNull { it.psi as? KtFunction }
                        .mapNotNull { it.valueParameters.find { it.name == typedRule.name }?.defaultValue }
                        .firstOrNull()?.let {
                            -" = "
                            -it
                        }
                }
            }
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

    //Constructors
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

    //lambda call
    handle<KtDotQualifiedExpression>(
        condition = {
            (typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.candidateDescriptor is FunctionInvokeDescriptor
        },
        priority = 2000,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val f = callExp.resolvedCall!!.candidateDescriptor as FunctionDescriptor
            out.addImport(f, f.tsName)

            val prop = nre.resolvedReferenceTarget as? ValueDescriptor
            if(prop != null){
                -VirtualGet(
                    receiver = typedRule.receiverExpression,
                    nameReferenceExpression = nre,
                    property = prop,
                    receiverType = typedRule.receiverExpression.resolvedExpressionTypeInfo?.type,
                    expr = typedRule,
                    safe = false
                )
            } else {
                -nre
            }
            -ArgumentsList(
                on = f,
                resolvedCall = callExp.resolvedCall!!
            )
        }
    )

    //Normal calls

    //Implicit receiver
    handle<KtCallExpression>(
        condition = {
            (typedRule.resolvedCall?.candidateDescriptor as? FunctionDescriptor)?.extensionReceiverParameter != null
        },
        priority = 1000
    ) {
        val nre = typedRule.calleeExpression as KtNameReferenceExpression
        val f = nre.resolvedReferenceTarget as FunctionDescriptor
        if (f is ConstructorDescriptor) {
            out.addImport(f.constructedClass)
        } else {
            out.addImport(f, f.tsName)
        }
        -f.tsName
        -ArgumentsList(
            on = f,
            resolvedCall = typedRule.resolvedCall!!,
            prependArguments = listOf(typedRule.getTsReceiver()!!)
        )
    }

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
            -ArgumentsList(
                on = f,
                resolvedCall = callExp.resolvedCall!!,
                prependArguments = listOf(typedRule.receiverExpression)
            )
        }
    )

    handle<KtSafeQualifiedExpression>(
        condition = {
            typedRule.selectorExpression is KtCallExpression
                    && typedRule.actuallyCouldBeExpression
                    && typedRule.parent !is KtSafeQualifiedExpression
        },
        priority = 100,
        action = {
            -'('
            doSuper()
            -" ?? null)"
        }
    )

    handle<KtSafeQualifiedExpression>(
        condition = {
            typedRule.selectorExpression is KtCallExpression
                    && typedRule.actuallyCouldBeExpression
                    && typedRule.resolvedExpressionTypeInfo?.type?.constructor?.declarationDescriptor?.fqNameSafe?.asString() == "kotlin.Unit"
            },
        priority = 101,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val f = callExp.resolvedCall!!.candidateDescriptor as FunctionDescriptor
            nullWrapAction(
                swiftTranslator = this@registerFunction,
                receiver = typedRule.receiverExpression,
                skip = false,
                type = typedRule.resolvedExpressionTypeInfo?.type,
                isExpression = typedRule.actuallyCouldBeExpression
            ){ rec ->
                -rec
                -'.'
                -nre
                -ArgumentsList(
                    on = f,
                    resolvedCall = callExp.resolvedCall!!
                )
            }
        }
    )

    handle<KtSafeQualifiedExpression>(
        condition = {
            ((typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.candidateDescriptor as? FunctionDescriptor)?.extensionReceiverParameter != null
        },
        priority = 1002,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val nre = callExp.calleeExpression as KtNameReferenceExpression
            val f = callExp.resolvedCall!!.candidateDescriptor as FunctionDescriptor
            out.addImport(f, f.tsName)

            nullWrapAction(
                swiftTranslator = this@registerFunction,
                receiver = typedRule.receiverExpression,
                skip = false,
                type = typedRule.resolvedExpressionTypeInfo?.type,
                isExpression = typedRule.actuallyCouldBeExpression
            ){ rec ->
                -nre
                -ArgumentsList(
                    on = f,
                    resolvedCall = callExp.resolvedCall!!,
                    prependArguments = listOf(rec ?: "")
                )
            }
        }
    )

    handle<KtCallExpression>(
        condition = { (typedRule.calleeExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget is FunctionDescriptor },
        priority = 1
    ) {
        val nre = typedRule.calleeExpression as KtNameReferenceExpression
        val f = nre.resolvedReferenceTarget as FunctionDescriptor
        if (f is ConstructorDescriptor) {
            out.addImport(f.constructedClass)
        } else {
            out.addImport(f, f.tsName)
        }
        -nre
        -ArgumentsList(
            on = f,
            resolvedCall = typedRule.resolvedCall!!
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
                resolvedCall = typedRule.resolvedCall!!,
                prependArguments = if (f.extensionReceiverParameter != null) listOf(typedRule.left!!) else listOf()
            )
        }
    )
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
                type = typedRule.resolvedExpressionTypeInfo?.type,
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
                type = typedRule.resolvedExpressionTypeInfo?.type,
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

            emitTemplate(
                requiresWrapping = typedRule.actuallyCouldBeExpression,
                type = typedRule.resolvedExpressionTypeInfo?.type?.makeNullable(),
                ensureReceiverNotNull = true,
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
                type = typedRule.resolvedExpressionTypeInfo?.type,
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
        if(typedRule.on.typeParametersCount > 0){
            -'<'
            typedRule.resolvedCall.typeArguments.entries
                .sortedBy { it.key.index }
                .forEachBetween(
                    forItem = { -(typedRule.resolvedCall.call.typeArguments.getOrNull(it.key.index) ?: it.value) },
                    between = { -", " }
                )
            -'>'
        }
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
        typedRule.resolvedCall.typeArguments.filter { it.key.isReified }.values.forEach {
            if (first) {
                first = false
            } else {
                -", "
            }
            -CompleteReflectableType(it)
        }
        typedRule.resolvedCall.valueArguments.entries.sortedBy { it.key.index }.forEach { (valueParam, value) ->
            if (first) {
                first = false
            } else {
                -", "
            }
            typedRule.replacements[valueParam]?.let {
                -it
            } ?: value.arguments.takeUnless { it.isEmpty() }?.let {
                it.forEachBetween(
                    forItem = { -(it.getArgumentExpression() ?: (if(valueParam.isVararg) "" else "undefined")) },
                    between = { -", " }
                )
            } ?: run {
                if(!valueParam.isVararg)
                    -"undefined"
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

data class ArgumentsList(
    val on: FunctionDescriptor,
    val resolvedCall: ResolvedCall<out CallableDescriptor>,
    val prependArguments: List<Any> = listOf(),
    val appendArguments: List<Any> = listOf(),
    val replacements: Map<ValueParameterDescriptor, Any?> = mapOf()
)
