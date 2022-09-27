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
import com.lightningkite.khrysalis.analysis.*
import org.jetbrains.kotlin.builtins.isFunctionType
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.resolve.source.getPsi

val FunctionDescriptor.tsNameOverridden: String?
    get() = if (this is ConstructorDescriptor) {
        if (!this.isPrimary) {
            this.tsConstructorName
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
            .joinToString("") { it.capitalize() }.let { "x$it" } +
                this.name.identifier.capitalize()
    } else this.overriddenDescriptors.asSequence().mapNotNull { it.tsNameOverridden }.firstOrNull()

val FunctionDescriptor.tsName: String?
    get() = tsNameOverridden ?: if (this.name.isSpecial) {
        null
    } else {
        this.name.identifier.safeJsIdentifier()
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
        when {
            body == null -> {
            }

            body is KtBlockExpression -> {
                -body
            }

            body is KtBinaryExpression
                    && body.operationToken == KtTokens.ELVIS
                    && body.right.let { it is KtThrowExpression } -> {
                -"{ \nconst result = "
                -body.left
                -";\nif (result === null) {\n"
                -body.right
                -"\n}\nreturn result; \n}"
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
        when {
            body == null -> {
            }

            body is KtBlockExpression -> {
                -body
            }

            body is KtBinaryExpression
                    && body.operationToken == KtTokens.ELVIS
                    && body.right.let { it is KtThrowExpression } -> {
                -"{ \nconst result = "
                -body.left
                -";\nif (result === null) {\n"
                -body.right
                -"\n}\nreturn result; \n}"
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
                            tr.containingClass()?.nameIdentifier ?: "any"
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
            if (typedRule.isTopLevel()) {
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

    handle<KtParameter>(
        condition = { typedRule.hasValOrVar() },
        priority = 1000,
        action = {
            -(typedRule.visibilityModifier() ?: "public")
            -" "
            if ((typedRule.valOrVarKeyword as? LeafPsiElement)?.elementType == KtTokens.VAL_KEYWORD) {
                -"readonly "
            }
            doSuper()
        }
    )

    handle<KtParameter> {
        -typedRule.nameIdentifier
        typedRule.typeReference?.let {
            -": "
            -it
        }
        val f = ((typedRule.parent as? KtParameterList)?.parent as? KtFunction)?.resolvedFunction
        if (f != null && f.modality == Modality.ABSTRACT) {
            //suppress defaults!
        } else {
            typedRule.defaultValue?.let {
                -" = "
                -it
            } ?: run {
                if (f != null) {
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

            val prop = nre.resolvedReferenceTarget as? ValueDescriptor
            if (prop != null) {
                -VirtualGet(
                    receiver = typedRule.replacementReceiverExpression,
                    nameReferenceExpression = nre,
                    property = prop,
                    receiverType = typedRule.receiverExpression.resolvedExpressionTypeInfo?.type,
                    expr = typedRule,
                    safe = false
                )
            } else if (nre.text == "invoke") {
                -typedRule.receiverExpression
            } else {
                -nre
            }
            -ArgumentsList(
                on = f,
                resolvedCall = callExp.resolvedCall!!
            )
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

            out.addImport("@lightningkite/khrysalis-runtime", "runOrNull")
            -"runOrNull("
            val prop = nre.resolvedReferenceTarget as? ValueDescriptor
            if (prop != null) {
                -VirtualGet(
                    receiver = typedRule.replacementReceiverExpression,
                    nameReferenceExpression = nre,
                    property = prop,
                    receiverType = typedRule.receiverExpression.resolvedExpressionTypeInfo?.type,
                    expr = typedRule,
                    safe = false
                )
            } else if (nre.text == "invoke") {
                -typedRule.receiverExpression
            } else {
                -nre
            }
            -", _ => _"
            -ArgumentsList(
                on = f,
                resolvedCall = callExp.resolvedCall!!
            )
            -")"
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
        -out.addImportGetName(f, f.tsName)
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
            if (f.dispatchReceiverParameter == null) {
                -out.addImportGetName(f, f.tsName)
            } else {
                -nre.getTsReceiver()
                -'.'
                -f.tsName
            }
            -ArgumentsList(
                on = f,
                resolvedCall = callExp.resolvedCall!!,
                prependArguments = listOf(typedRule.replacementReceiverExpression)
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
                receiver = typedRule.replacementReceiverExpression,
                skip = false,
                type = typedRule.resolvedExpressionTypeInfo?.type,
                isExpression = typedRule.actuallyCouldBeExpression
            ) { rec ->
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

            nullWrapAction(
                swiftTranslator = this@registerFunction,
                receiver = typedRule.replacementReceiverExpression,
                skip = false,
                type = typedRule.resolvedExpressionTypeInfo?.type,
                isExpression = typedRule.actuallyCouldBeExpression
            ) { rec ->
                if (f.dispatchReceiverParameter == null) {
                    -out.addImportGetName(f, f.tsName)
                } else {
                    -nre
                }
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
        if (f.dispatchReceiverParameter != null) {
            -nre
        } else {
            if (f is ConstructorDescriptor) {
                if (f.isPrimary) {
                    -nre
                } else {
                    -nre
                    -'.'
                    -f.tsName
                }
            } else {
                -out.addImportGetName(f, f.tsName)
            }
        }
        -ArgumentsList(
            on = f,
            resolvedCall = typedRule.resolvedCall!!
        )
    }

    // lambda-ish calls
    handle<KtCallExpression>(
        condition = {
            val target = (typedRule.calleeExpression as? KtReferenceExpression)?.resolvedReferenceTarget
            target is ValueDescriptor && !target.type.isFunctionType
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
                -typedRule.operationReference.text
            } else if (f.dispatchReceiverParameter != null) {
                -typedRule.left
                -"."
                -typedRule.operationReference.text
            } else {
                -out.addImportGetName(f, f.tsName)
            }
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
            replacements.getCall(typedRule.resolvedCall ?: return@handle false) != null
        },
        priority = 10_000,
        action = {
            val resolvedCall = typedRule.resolvedCall!!
            val rule = replacements.getCall(resolvedCall)!!

            emitTemplate(
                requiresWrapping = typedRule.actuallyCouldBeExpression,
                type = typedRule.resolvedExpressionTypeInfo?.type,
                template = rule.template,
                receiver = typedRule.left,
                dispatchReceiver = typedRule.operationReference.getTsReceiver() ?: typedRule.left,
                allParameters = { typedRule.right },
                parameter = resolvedCall.template_parameter,
                typeParameter = resolvedCall.template_typeParameter,
                parameterByIndex = resolvedCall.template_parameterByIndex,
                typeParameterByIndex = resolvedCall.template_typeParameterByIndex,
                reifiedTypeParameterByIndex = resolvedCall.template_reifiedTypeParameterByIndex,
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
                type = typedRule.resolvedExpressionTypeInfo?.type,
                template = rule.template,
                receiver = if (typedRule.hasNewlineBeforeAccess && rule.template.isThisDot)
                    listOf(typedRule.replacementReceiverExpression, "\n")
                else typedRule.replacementReceiverExpression,
                dispatchReceiver = nre.getTsReceiver(),
                extensionReceiver = typedRule.replacementReceiverExpression,
                allParameters = resolvedCall.template_allParameter,
                parameter = resolvedCall.template_parameter,
                typeParameter = resolvedCall.template_typeParameter,
                parameterByIndex = resolvedCall.template_parameterByIndex,
                typeParameterByIndex = resolvedCall.template_typeParameterByIndex,
                reifiedTypeParameterByIndex = resolvedCall.template_reifiedTypeParameterByIndex,
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

            emitTemplate(
                requiresWrapping = typedRule.actuallyCouldBeExpression,
                type = typedRule.resolvedExpressionTypeInfo?.type?.makeNullable(),
                ensureReceiverNotNull = true,
                template = rule.template,
                receiver = if (typedRule.hasNewlineBeforeAccess && rule.template.isThisDot)
                    listOf(typedRule.replacementReceiverExpression, "\n")
                else typedRule.replacementReceiverExpression,
                dispatchReceiver = nre.getTsReceiver(),
                extensionReceiver = if (typedRule.hasNewlineBeforeAccess && rule.template.isThisDot)
                    listOf(typedRule.replacementReceiverExpression, "\n")
                else typedRule.replacementReceiverExpression,
                allParameters = resolvedCall.template_allParameter,
                parameter = resolvedCall.template_parameter,
                typeParameter = resolvedCall.template_typeParameter,
                parameterByIndex = resolvedCall.template_parameterByIndex,
                typeParameterByIndex = resolvedCall.template_typeParameterByIndex,
                reifiedTypeParameterByIndex = resolvedCall.template_reifiedTypeParameterByIndex,
            )
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
                type = typedRule.resolvedExpressionTypeInfo?.type,
                template = rule.template,
                receiver = nre.getTsReceiver(),
                dispatchReceiver = nre.getTsReceiver(),
                allParameters = resolvedCall.template_allParameter,
                parameter = resolvedCall.template_parameter,
                typeParameter = resolvedCall.template_typeParameter,
                parameterByIndex = resolvedCall.template_parameterByIndex,
                typeParameterByIndex = resolvedCall.template_typeParameterByIndex,
                reifiedTypeParameterByIndex = resolvedCall.template_reifiedTypeParameterByIndex,
            )
        }
    )

    handle<ArgumentsList>(
        condition = {
            typedRule.on.name.asString() == "copy" &&
                    (typedRule.on.containingDeclaration as? ClassDescriptor)?.isData == true &&
                    typedRule.on.source.getPsi() !is KtNamedFunction
        },
        priority = 1,
        action = {
            -"({"
            typedRule.resolvedCall.valueArguments.entries.filter { it.value.arguments.isNotEmpty() }.forEachBetween(
                forItem = { arg ->
                    -'"'
                    -arg.key.name.asString().safeJsIdentifier()
                    -"\": "
                    if (arg.key.isVararg) {
                        -arg.value.arguments.forEachBetween(
                            forItem = { -it.getArgumentExpression() },
                            between = { -", " }
                        )
                    } else {
                        -arg.value.arguments.firstOrNull()?.getArgumentExpression()
                    }
                },
                between = { -", " }
            )
            -"})"
        }
    )

    handle<ArgumentsList> {
        if (typedRule.on.typeParametersCount > 0 && !typedRule.suppressTypeArgs) {
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
                    forItem = { -(it.getArgumentExpression() ?: (if (valueParam.isVararg) "" else "undefined")) },
                    between = { -", " }
                )
            } ?: run {
                if (!valueParam.isVararg)
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
    val replacements: Map<ValueParameterDescriptor, Any?> = mapOf(),
    val suppressTypeArgs: Boolean = false
)
