package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.swift.replacements.Template
import com.lightningkite.khrysalis.swift.replacements.TemplatePart
import com.lightningkite.khrysalis.util.AnalysisExtensions
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
import org.jetbrains.kotlin.lexer.KtToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.psi.synthetics.SyntheticClassOrObjectDescriptor
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.VariableAsFunctionResolvedCall
import org.jetbrains.kotlin.resolve.calls.resolvedCallUtil.getImplicitReceiverValue
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.synthetic.SyntheticJavaPropertyDescriptor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isNullable
import java.util.concurrent.atomic.AtomicInteger

val uniqueNumber = AtomicInteger(0)

data class VirtualGet(
    val receiver: Any,
    val property: PropertyDescriptor,
    val receiverType: KotlinType?,
    val expr: KtQualifiedExpression,
    val safe: Boolean
)

data class VirtualSet(
    val receiver: Any,
    val property: PropertyDescriptor,
    val receiverType: KotlinType?,
    val expr: KtExpression,
    val safe: Boolean,
    val value: Any,
    val dispatchReceiver: String?
)

val PropertyDescriptor.hasSwiftBacking: Boolean
    get() {
        return hasSwiftOverride || ((this.getter?.isDefault == false || this.setter?.isDefault == false) && this.backingField != null && this.extensionReceiverParameter == null)
    }
val PropertyDescriptor.hasSwiftOverride: Boolean
    get() {
        return overriddenDescriptors
            .any { (it.containingDeclaration as? ClassDescriptor)?.kind != ClassKind.INTERFACE }
    }


fun AnalysisExtensions.capturesSelf(
    it: PsiElement?,
    containingDeclaration: ClassDescriptor?,
    immediate: Boolean = false
): Boolean {
    if (it == null) return false
    if (it is KtLambdaExpression) {
        return it.allChildren.any { capturesSelf(it, containingDeclaration, false) }
    }
    if (it is KtExpression) {
        var hasThis = false
        val resolved: CallableDescriptor?
        if (it is KtThisExpression) {
            hasThis = true
            resolved = (it.parent as? KtExpression)?.resolvedCall?.candidateDescriptor
        } else {
            resolved = it.resolvedCall?.candidateDescriptor
            hasThis = when (val r = it.resolvedCall) {
                is VariableAsFunctionResolvedCall -> r.variableCall.getImplicitReceiverValue() != null
                else -> r?.getImplicitReceiverValue() != null
            }
        }
        if (hasThis) {
            if (resolved is PropertyDescriptor) {
                val safe = immediate && resolved.getter?.isDefault != false && resolved.overriddenDescriptors.isEmpty()
                return !safe
            } else {
                return true
            }
        }
    }
    return it.allChildren.any { capturesSelf(it, containingDeclaration, immediate) }
}

fun SwiftTranslator.registerVariable() {

    //If we belong to an interface, skip the implementations
    handle<KtProperty>(
        condition = {
            typedRule.parentOfType<KtClassBody>()?.parentOfType<KtClass>()?.isInterface() == true
        },
        priority = 150,
        action = {
            -"var "
            -typedRule.nameIdentifier
            -": "
            -typedRule.typeReference
            if (typedRule.isVar) {
                -" { get set }"
            } else {
                -" { get }"
            }
            -"\n"
            val tr = typedRule
            val resolved = tr.resolvedProperty ?: return@handle
            val ktClassBody = typedRule.parentOfType<KtClassBody>()!!
            val ktClass = ktClassBody.parentOfType<KtClass>()!!
            if (typedRule.getter != null || typedRule.setter != null) {
                ktClassBody.addPostAction {
                    -"\n"
                    -"var "
                    -tr.nameIdentifier
                    tr.typeReference?.let {
                        -": "
                        -it
                    } ?: run {
                        -": "
                        -tr.resolvedProperty?.type
                    }
                    -" {\n"
                    -tr.getter
                    -tr.setter
                    -"}"
                }
            }
        }
    )

    //abstract
    handle<KtProperty>(
        condition = {
            typedRule.hasModifier(KtTokens.ABSTRACT_KEYWORD)
        },
        priority = 150,
        action = {
            if (typedRule.isMember) {
                if (typedRule.resolvedProperty?.overriddenDescriptors
                        ?.any { (it.containingDeclaration as? ClassDescriptor)?.kind != ClassKind.INTERFACE } == true
                ) {
                    -"override "
                }
            }
            if (typedRule.isMember || (typedRule.isTopLevel && !typedRule.isExtensionDeclaration())) {
                -(typedRule.swiftVisibility() ?: "public")
                -" "
            }
            -"var "
            -typedRule.nameIdentifier
            -": "
            -typedRule.typeReference
            if (typedRule.isVar) {
                -" { get { TODO() } set { TODO() } }"
            } else {
                -" { get { TODO() } }"
            }
        }
    )

    //Local with guard return
    handle<KtProperty>(
        condition = {
            typedRule.isLocal && (typedRule.initializer as? KtBinaryExpression)?.let {
                it.operationToken == KtTokens.ELVIS && (it.right is KtReturnExpression || it.right is KtContinueExpression || it.right is KtBreakExpression)
            } == true
        },
        priority = 50,
        action = {
            -"guard "
            if (typedRule.isVar || (typedRule.resolvedProperty?.type?.requiresMutable()
                    ?: typedRule.resolvedVariable?.type?.requiresMutable()) == true
            ) {
                -"var "
            } else {
                -"let "
            }
            -typedRule.nameIdentifier
            val type = typedRule.typeReference
            type?.let {
                -": "
                -it
            }
            val elvis = typedRule.initializer as KtBinaryExpression
            -" = "
            -elvis.left
            -" else { "
            -elvis.right
            -" }"
        }
    )
    //Weak
    handle<KtProperty>(
        condition = {
            ((typedRule.delegateExpression as? KtCallExpression)?.calleeExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget?.fqNameOrNull()
                ?.asString() == "com.lightningkite.khrysalis.weak"
        },
        priority = 15
    ) {
        -"weak "
        if (typedRule.isMember || (typedRule.isTopLevel && !typedRule.isExtensionDeclaration())) {
            -(typedRule.swiftVisibility() ?: "public")
            -" "
        }
        -"var "
        -typedRule.nameIdentifier
        typedRule.typeReference?.let {
            -": "
            -it
        } ?: run {
            if (typedRule.isMember || typedRule.initializer == null) {
                -": "
                -typedRule.resolvedProperty?.type
            }
        }
        typedRule.delegateExpression?.let {
            -" = "
            -(it as KtCallExpression).valueArguments.first().getArgumentExpression()
        }
    }
    //Plain
    handle<KtProperty> {
        if (typedRule.annotationEntries.any { it.typeReference?.text?.endsWith("weak") == true }) {
            -"weak "
        }
        if (typedRule.isMember || (typedRule.isTopLevel && !typedRule.isExtensionDeclaration())) {
            -(typedRule.swiftVisibility() ?: "public")
            -" "
        }
        val isLateInit = typedRule.hasModifier(KtTokens.LATEINIT_KEYWORD) || (typedRule.isMember && capturesSelf(
            typedRule.initializer,
            typedRule.containingClassOrObject?.resolvedClass
        ))
        if (isLateInit || typedRule.isVar || (typedRule.resolvedProperty?.type?.requiresMutable()
                ?: typedRule.resolvedVariable?.type?.requiresMutable()) == true
        ) {
            -"var "
        } else {
            -"let "
        }
        -typedRule.nameIdentifier
        val type = typedRule.typeReference ?: if (typedRule.isMember || typedRule.initializer == null) {
            typedRule.resolvedProperty?.type
        } else null
        type?.let {
            -": "
            if (isLateInit && typedRule.resolvedProperty?.type?.isNullable() == false) {
                -'('
                -it
                -")!"
            } else {
                -it
            }
        }
        if (!typedRule.isMember) {
            typedRule.initializer?.let {
                -" = "
                -it
            }
        }
    }
    //Virtual
    handle<KtProperty>(
        condition = { typedRule.initializer == null && typedRule.getter != null },
        priority = 15,
        action = {
            if (typedRule.isMember) {
                if (typedRule.resolvedProperty?.hasSwiftOverride == true) {
                    -"override "
                }
            }
            if (typedRule.isMember || (typedRule.isTopLevel && !typedRule.isExtensionDeclaration())) {
                -(typedRule.swiftVisibility() ?: "public")
                -" "
            }
            -"var "
            -typedRule.nameIdentifier
            typedRule.typeReference?.let {
                -": "
                -it
            } ?: run {
                -": "
                -typedRule.resolvedProperty?.type
            }
            -" {\n"
            -typedRule.getter
            -typedRule.setter
            -"}"
        }
    )
    //Partial
    handle<KtProperty>(
        condition = { typedRule.resolvedProperty?.hasSwiftBacking == true },
        priority = 10,
        action = {
            val isLateInit = typedRule.hasModifier(KtTokens.LATEINIT_KEYWORD) || (typedRule.isMember && capturesSelf(
                typedRule.initializer,
                typedRule.containingClassOrObject?.resolvedClass
            ))
            if (typedRule.isMember || (typedRule.isTopLevel && !typedRule.isExtensionDeclaration())) {
                -(typedRule.swiftVisibility() ?: "public")
                -" "
            }
            -"var _"
            -typedRule.nameIdentifier
            typedRule.typeReference?.let {
                -": "
                if (isLateInit && typedRule.resolvedProperty?.type?.isNullable() == false) {
                    -'('
                    -it
                    -")!"
                } else {
                    -it
                }
            } ?: run {
                if (typedRule.isMember || typedRule.initializer == null) {
                    -": "
                    -typedRule.resolvedProperty?.type
                }
            }
            if (!typedRule.isMember) {
                typedRule.initializer?.let {
                    -" = "
                    -it
                }
            }
            -"\n"
            if (typedRule.isMember) {
                if (typedRule.resolvedProperty?.hasSwiftOverride == true) {
                    -"override "
                }
            }
            if (typedRule.isMember || (typedRule.isTopLevel && !typedRule.isExtensionDeclaration())) {
                -(typedRule.swiftVisibility() ?: "public")
                -" "
            }
            -"var "
            -typedRule.nameIdentifier
            typedRule.typeReference?.let {
                -": "
                -it
            } ?: run {
                -": "
                -typedRule.resolvedProperty?.type
            }
            -" {\n"
            typedRule.getter?.let {
                -it
            } ?: run {
                -"get { return _"
                -typedRule.nameIdentifier
                -" }"
                -"\n"
            }
            if (typedRule.isVar || typedRule.resolvedProperty?.type?.requiresMutable() == true) {
                typedRule.setter?.let {
                    -it
                } ?: run {
                    -"set(value) { _"
                    -typedRule.nameIdentifier
                    -" = value }"
                    -"\n"
                }
            }
            -"}"
        }
    )
    //Extension
    handle<KtProperty>(
        condition = {
            typedRule.receiverTypeReference != null
                    && typedRule.resolvedProperty?.worksAsSwiftConstraint() == true
                    && typedRule.containingClassOrObject == null
        },
        priority = 100,
        action = {
            if (typedRule.isMember || typedRule.isTopLevel) {
                -(typedRule.swiftVisibility() ?: "public")
                -" "
            }
            -SwiftExtensionStart(
                typedRule.resolvedProperty!!,
                typedRule.receiverTypeReference,
                typedRule.typeParameterList
            )
            -'\n'
            doSuper()
            -"\n}"
        }
    )
    //Extension Jank
    handle<KtProperty>(
        condition = {
            typedRule.resolvedProperty?.tsFunctionGetName != null
        },
        priority = 99,
        action = {
            withReceiverScope(typedRule.resolvedProperty!!) {
                if (typedRule.isMember || typedRule.isTopLevel) {
                    -(typedRule.swiftVisibility() ?: "public")
                    -" "
                }
                -VirtualFunction(
                    typedRule.resolvedProperty!!.tsFunctionGetName!!,
                    resolvedFunction = null,
                    typeParameters = typedRule.typeParameters,
                    valueParameters = listOfNotNull(typedRule.receiverTypeReference?.let { listOf("_ this: ", it) }),
                    returnType = typedRule.typeReference ?: typedRule.resolvedProperty?.type!!,
                    body = typedRule.getter?.bodyExpression
                )
                -"\n"
                if (typedRule.setter != null) {
                    if (typedRule.isMember || typedRule.isTopLevel) {
                        -(typedRule.swiftVisibility() ?: "public")
                        -" "
                    }
                    -VirtualFunction(
                        typedRule.resolvedProperty!!.tsFunctionSetName!!,
                        resolvedFunction = null,
                        typeParameters = typedRule.typeParameters,
                        valueParameters = listOfNotNull(
                            typedRule.receiverTypeReference?.let { listOf("_ this: ", it) },
                            listOf(
                                "_ ",
                                typedRule.setter!!.parameter!!.nameIdentifier,
                                ": ",
                                typedRule.typeReference ?: typedRule.resolvedProperty?.type!!
                            )
                        ),
                        returnType = "Void",
                        body = typedRule.setter?.bodyExpression
                    )
                }
            }
        }
    )

    //Member getter/setter
    handle<KtPropertyAccessor>(
        condition = { typedRule.isGetter },
        priority = 4,
        action = {
            -"get "
            val body = typedRule.bodyExpression
            if (body is KtBlockExpression) {
                -body
            } else {
                -"{ return "
                -body
                -" }"
            }
            -"\n"
        }
    )
    handle<KtPropertyAccessor>(
        condition = { typedRule.isSetter },
        priority = 3,
        action = {
            -" set"
            -"("
            -(typedRule.parameter?.nameIdentifier ?: -"value")
            -") "
            -typedRule.bodyBlockExpression
            -"\n"
        }
    )

    //'field' access
    handle<KtNameReferenceExpression>(
        condition = { typedRule.text == "field" && typedRule.parentOfType<KtPropertyAccessor>() != null },
        priority = 1000,
        action = {
            val prop = typedRule.parentOfType<KtPropertyAccessor>()!!
            if (prop.property.isMember) {
                -"self."
            }
            -"_"
            -typedRule.parentOfType<KtPropertyAccessor>()!!.property.nameIdentifier
        }
    )

    //Getter usage
    handle<VirtualGet>(
        condition = {
            replacements.getGet(typedRule.property, typedRule.receiverType) != null
        },
        priority = 10_000,
        action = {
            val rule = replacements.getGet(typedRule.property, typedRule.receiverType)!!

            nullWrapAction(this@registerVariable, typedRule.expr) { rec, mode ->
                emitTemplate(
                    requiresWrapping = typedRule.expr.actuallyCouldBeExpression,
                    template = if (mode == AccessMode.QUEST_DOT)
                        Template(parts = rule.template.parts.toMutableList().apply {
                            this[1] = (this[1] as TemplatePart.Text).let { it.copy("?" + it.string) }
                        })
                    else rule.template,
                    receiver = rec,
                    dispatchReceiver = typedRule.expr.getTsReceiver()
                )
            }
        }
    )
    handle<VirtualGet>(
        condition = {
            typedRule.property.tsFunctionGetName != null
        },
        priority = 1000,
        action = {
            nullWrapAction(this@registerVariable, typedRule.expr) { rec, mode ->
                if (typedRule.property.dispatchReceiverParameter != null) {
                    -typedRule.expr.getTsReceiver()
                    -"."
                }
                -typedRule.property.tsFunctionGetName
                -'('
                -rec
                -')'
            }
        }
    )
    handle<VirtualGet> {
        nullWrapAction(this@registerVariable, typedRule.expr) { rec, mode ->
            -rec
            if (mode == AccessMode.QUEST_DOT) {
                -"?."
            } else {
                -"."
            }
            -typedRule.property.name.asString()
        }
    }
    handle<KtDotQualifiedExpression>(
        condition = { ((typedRule.selectorExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? PropertyDescriptor) != null },
        priority = 1000,
        action = {
            val p =
                (typedRule.selectorExpression as KtNameReferenceExpression).resolvedReferenceTarget as PropertyDescriptor
            -VirtualGet(
                receiver = typedRule.receiverExpression,
                property = p,
                receiverType = typedRule.receiverExpression.resolvedExpressionTypeInfo?.type,
                expr = typedRule,
                safe = false
            )
        }
    )
    handle<KtSafeQualifiedExpression>(
        condition = { ((typedRule.selectorExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? PropertyDescriptor) != null },
        priority = 1000,
        action = {
            val p =
                (typedRule.selectorExpression as KtNameReferenceExpression).resolvedReferenceTarget as PropertyDescriptor
            -VirtualGet(
                receiver = typedRule.receiverExpression,
                property = p,
                receiverType = typedRule.receiverExpression.resolvedExpressionTypeInfo?.type,
                expr = typedRule,
                safe = true
            )
        }
    )

    handle<KtNameReferenceExpression>(
        condition = { (typedRule.resolvedReferenceTarget as? PropertyDescriptor)?.tsFunctionGetName != null },
        priority = 100,
        action = {
            val prop = typedRule.resolvedReferenceTarget as PropertyDescriptor
            -prop.tsFunctionGetName
            when {
                prop.extensionReceiverParameter == null -> -"()"
                else -> {
                    -'('
                    -(typedRule.getTsReceiver())
                    -')'
                }
            }
        }
    )
    handle<KtNameReferenceExpression>(
        condition = {
            val pd = typedRule.resolvedReferenceTarget as? PropertyDescriptor ?: return@handle false
            replacements.getGet(pd) != null
        },
        priority = 10_000,
        action = {
            val pd = typedRule.resolvedReferenceTarget as PropertyDescriptor
            val rule = replacements.getGet(pd)!!
            emitTemplate(
                requiresWrapping = typedRule.actuallyCouldBeExpression,
                template = rule.template,
                receiver = typedRule.getTsReceiver()
            )
        }
    )

    //Setter usage
    handle<VirtualSet>(
        condition = {
            typedRule.property.tsFunctionSetName != null
        },
        priority = 1000,
        action = {

            if (typedRule.property.dispatchReceiverParameter != null) {
                -typedRule.dispatchReceiver
                -"."
            }
            -typedRule.property.tsFunctionSetName
            -'('
            -(if (typedRule.safe) run {
                -"if let"
                val n = "temp${uniqueNumber.getAndIncrement()}"
                -n
                -" = "
                -typedRule.receiver
                n
            } else typedRule.receiver)
            -", "
            -typedRule.value
            -')'
        }
    )
    handle<VirtualSet>(
        condition = {
            replacements.getSet(typedRule.property) != null
        },
        priority = 10_000,
        action = {
            val rule = replacements.getSet(typedRule.property)!!
            emitTemplate(
                requiresWrapping = false,
                template = rule.template,
                receiver = typedRule.receiver,
                dispatchReceiver = typedRule.dispatchReceiver,
                extensionReceiver = null,
                value = typedRule.value
            )
        }
    )
    handle<VirtualSet>(condition = { typedRule.safe }, priority = 1) {
        -typedRule.receiver
        -"?."
        -typedRule.property.name.asString()
        -" = "
        -typedRule.value
    }
    handle<VirtualSet> {
        -typedRule.receiver
        -"."
        -typedRule.property.name.asString()
        -" = "
        -typedRule.value
    }
    handle<KtBinaryExpression>(
        condition = {
            val left = typedRule.left as? KtQualifiedExpression ?: return@handle false
            val nre = left.selectorExpression as? KtNameReferenceExpression ?: return@handle false
            val pd = nre.resolvedReferenceTarget as? PropertyDescriptor ?: return@handle false
            typedRule.resolvedVariableReassignment == true
        },
        priority = 50,
        action = {
            val left = (typedRule.left as KtQualifiedExpression)
            val nre = left.selectorExpression as KtNameReferenceExpression
            val leftProp = nre.resolvedReferenceTarget as PropertyDescriptor
            val rec: Any = if (typedRule.operationToken == KtTokens.EQ)
                left.receiverExpression
            else {
                if (left.receiverExpression.isSimple()) {
                    left.receiverExpression
                } else {
                    val n = "temp${uniqueNumber.getAndIncrement()}"
                    -"let $n = "
                    -left.receiverExpression
                    -"\n"
                    n
                }
            }

            -VirtualSet(
                receiver = rec,
                property = leftProp,
                receiverType = left.receiverExpression.resolvedExpressionTypeInfo?.type,
                expr = typedRule,
                dispatchReceiver = nre.getTsReceiver(),
                safe = false,
                value = if (typedRule.operationToken == KtTokens.EQ) {
                    typedRule.right!!
                } else {
                    ValueOperator(
                        left = VirtualGet(
                            receiver = rec,
                            property = leftProp,
                            receiverType = left.receiverExpression.resolvedExpressionTypeInfo?.type,
                            expr = left,
                            safe = false
                        ),
                        right = typedRule.right!!,
                        functionDescriptor = typedRule.operationReference.resolvedReferenceTarget as FunctionDescriptor,
                        dispatchReceiver = typedRule.getTsReceiver(),
                        operationToken = typedRule.operationToken,
                        resolvedCall = typedRule.resolvedCall
                    )
                }
            )
        }
    )
    handle<KtBinaryExpression>(
        condition = {
            val left = typedRule.left as? KtNameReferenceExpression ?: return@handle false
            (left.resolvedReferenceTarget as? PropertyDescriptor)?.tsFunctionSetName != null && typedRule.resolvedVariableReassignment == true
        },
        priority = 100,
        action = {
            val left = (typedRule.left as KtNameReferenceExpression)
            val leftProp = left.resolvedReferenceTarget as PropertyDescriptor
            -leftProp.tsFunctionSetName
            when {
                leftProp.extensionReceiverParameter == null -> {
                    -'('
                }
                else -> {
                    -'('
                    -(left.getTsReceiver())
                    -", "
                }
            }
            if (typedRule.operationToken == KtTokens.EQ) {
                -typedRule.right!!
            } else {
                -ValueOperator(
                    left = typedRule.left!!,
                    right = typedRule.right!!,
                    functionDescriptor = typedRule.operationReference.resolvedReferenceTarget as FunctionDescriptor,
                    dispatchReceiver = typedRule.getTsReceiver(),
                    operationToken = typedRule.operationToken,
                    resolvedCall = typedRule.resolvedCall
                )
            }
            -')'
        }
    )
    handle<KtBinaryExpression>(
        condition = {
            val left = typedRule.left as? KtNameReferenceExpression ?: return@handle false
            val pd = (left.resolvedReferenceTarget as? PropertyDescriptor) ?: return@handle false
            replacements.getSet(pd) != null && typedRule.resolvedVariableReassignment == true
        },
        priority = 10_000,
        action = {
            val nre = typedRule.left as KtNameReferenceExpression
            val pd = (nre.resolvedReferenceTarget as PropertyDescriptor)
            val rule = replacements.getSet(pd)!!
            emitTemplate(
                requiresWrapping = typedRule.actuallyCouldBeExpression,
                template = rule.template,
                receiver = nre.getTsReceiver(),
                extensionReceiver = null,
                value = typedRule.right
            )
        }
    )
}

val PropertyDescriptor.tsFunctionGetName: String?
    get() = when {
        this.worksAsSwiftConstraint() && this.containingDeclaration !is ClassDescriptor -> null
        this is SyntheticJavaPropertyDescriptor -> null
        extensionReceiverParameter != null -> "get" + extensionReceiverParameter!!
            .value
            .type
            .getJetTypeFqName(false)
            .split('.')
            .joinToString("") { it.capitalize() }
            .plus(this.name.identifier.capitalize())
        else -> when (this.containingDeclaration) {
            is ClassDescriptor -> null
            is SyntheticClassOrObjectDescriptor -> null
            else -> null
        }
    }
val PropertyDescriptor.tsFunctionSetName: String?
    get() = when {
        this.worksAsSwiftConstraint() && this.containingDeclaration !is ClassDescriptor -> null
        this is SyntheticJavaPropertyDescriptor -> null
        extensionReceiverParameter != null -> "set" + extensionReceiverParameter!!
            .value
            .type
            .getJetTypeFqName(false)
            .split('.')
            .joinToString("") { it.capitalize() }
            .plus(this.name.identifier.capitalize())
        else -> when (this.containingDeclaration) {
            is ClassDescriptor -> null
            is SyntheticClassOrObjectDescriptor -> null
            else -> null
        }
    }

inline fun <reified T : PsiElement> PsiElement.parentOfType(): T? = parentOfType(T::class.java)
fun <T : PsiElement> PsiElement.parentOfType(type: Class<T>): T? =
    if (type.isInstance(this.parent)) type.cast(this.parent) else this.parent?.parentOfType(type)