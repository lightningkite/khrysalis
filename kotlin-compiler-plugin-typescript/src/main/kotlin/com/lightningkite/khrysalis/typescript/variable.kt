package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.analysis.*
import com.lightningkite.khrysalis.typescript.manifest.declaresPrefix
import com.lightningkite.khrysalis.util.forEachBetween
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import com.lightningkite.khrysalis.util.simpleFqName
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.ValueDescriptor
import org.jetbrains.kotlin.descriptors.impl.SyntheticFieldDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.load.java.descriptors.JavaPropertyDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.psi.synthetics.SyntheticClassOrObjectDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtension
import org.jetbrains.kotlin.synthetic.SyntheticJavaPropertyDescriptor
import org.jetbrains.kotlin.types.KotlinType
import java.util.concurrent.atomic.AtomicInteger

data class VirtualGet(
    val receiver: Any? = null,
    val nameReferenceExpression: KtNameReferenceExpression,
    val property: ValueDescriptor,
    val receiverType: KotlinType?,
    val expr: KtExpression,
    val safe: Boolean
) {
    fun extensionReceiver(typescriptTranslator: TypescriptTranslator): Any? = with(typescriptTranslator) {
        when {
            property.isExtension -> receiver ?: nameReferenceExpression.getTsReceiver()
            else -> null
        }
    }

    fun dispatchReceiver(typescriptTranslator: TypescriptTranslator): Any? = with(typescriptTranslator) {
        when {
            property is JavaPropertyDescriptor -> receiver ?: nameReferenceExpression.getTsReceiver()
            property.dispatchReceiverParameter == null -> null
            property.isExtension -> nameReferenceExpression.getTsReceiver()
            else -> receiver ?: nameReferenceExpression.getTsReceiver()
        }
    }
}

data class VirtualSet(
    val receiver: Any? = null,
    val nameReferenceExpression: KtNameReferenceExpression,
    val property: ValueDescriptor,
    val receiverType: KotlinType?,
    val expr: KtExpression,
    val safe: Boolean,
    val value: Any
) {
    fun extensionReceiver(typescriptTranslator: TypescriptTranslator): Any? = with(typescriptTranslator) {
        when {
            property.isExtension -> receiver ?: nameReferenceExpression.getTsReceiver()
            else -> null
        }
    }

    fun dispatchReceiver(typescriptTranslator: TypescriptTranslator): Any? = with(typescriptTranslator) {
        when {
            property is JavaPropertyDescriptor -> receiver ?: nameReferenceExpression.getTsReceiver()
            property.dispatchReceiverParameter == null -> null
            property.isExtension -> nameReferenceExpression.getTsReceiver()
            else -> receiver ?: nameReferenceExpression.getTsReceiver()
        }
    }
}

fun TypescriptTranslator.registerVariable() {

    //If we belong to an interface, skip the implementations
    handle<KtProperty>(
        condition = {
            typedRule.let { it.parent as? KtClassBody }?.let { it.parent as? KtClass }?.isInterface() == true
        },
        priority = 150,
        action = {
            if (!typedRule.isVar) {
                -"readonly "
            }
            -typedRule.nameIdentifier
            -": "
            -typedRule.typeReference
            -";\n"
            val tr = typedRule
            val resolved = tr.resolvedProperty ?: return@handle
            val ktClassBody = typedRule.parentOfType<KtClassBody>()!!
            val ktClass = ktClassBody.parentOfType<KtClass>()!!
            val resolvedClass = ktClass.resolvedClass ?: return@handle
            tr.getter?.let { getter ->
                ktClassBody.addPostAction {
                    -"\nexport function "
                    -resolved.tsFunctionGetDefaultName
                    (ktClass.typeParameters + tr.typeParameters).takeUnless { it.isEmpty() }?.let {
                        -'<'
                        it.forEachBetween(
                            forItem = { -it },
                            between = { -", " }
                        )
                        -'>'
                    }
                    withReceiverScope(resolvedClass) { r ->
                        -'('
                        -r
                        -": "
                        -ktClass.nameIdentifier
                        -"): "
                        -tr.typeReference
                        -" "
                        val body = getter.bodyExpression
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
            tr.setter?.let { setter ->
                ktClassBody.addPostAction {
                    -"\nexport function "
                    -resolved.tsFunctionSetDefaultName
                    (ktClass.typeParameters + tr.typeParameters).takeUnless { it.isEmpty() }?.let {
                        -'<'
                        it.forEachBetween(
                            forItem = { -it },
                            between = { -", " }
                        )
                        -'>'
                    }
                    withReceiverScope(resolvedClass) { r ->
                        -'('
                        -r
                        -": "
                        -ktClass.nameIdentifier
                        -", "
                        -(setter.parameter?.name ?: "value")
                        -": "
                        -tr.typeReference
                        -") "
                        val body = setter.bodyExpression
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
        }
    )

    //Handle special case of completely virtual property
    handle<KtProperty>(
        condition = { typedRule.getter != null && (!typedRule.isVar || typedRule.setter != null) && typedRule.initializer == null && !typedRule.isLazy },
        priority = 100,
        action = {
            -"$declaresPrefix${typedRule.simpleFqName}\n"
            -typedRule.getter!!
            -typedRule.setter
        }
    )

    handle<KtProperty> {
        val tsVar = (typedRule.isVar || typedRule.isLazy)
        if (typedRule.isMember) {
            -(typedRule.visibilityModifier() ?: "public")
            -" "
            if (typedRule.hasModifier(KtTokens.ABSTRACT_KEYWORD)) {
                -"abstract "
            }
            if (!tsVar) {
                -"readonly "
            }
        } else {
            if (typedRule.isTopLevel && !typedRule.isPrivate()) {
                -"$declaresPrefix${typedRule.simpleFqName}\n"
                -"export "
            }
            if (tsVar) {
                -"let "
            } else {
                -"const "
            }
        }
        val requiresBackingField =
            typedRule.getter != null || typedRule.setter != null || (!typedRule.isPrivate() && typedRule.isTopLevel) || typedRule.isLazy
        if (requiresBackingField) {
            -"_"
        }
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
        if (!typedRule.isMember) {
            typedRule.initializer?.let {
                -" = "
                -it
            }
        }
        -";"
        if (typedRule.getter != null || typedRule.setter != null || requiresBackingField) {
            typedRule.getter?.let {
                -"\n"
                -it
            } ?: run {
                -"\n"
                fun lazy(selfRef: () -> Unit) {
                    -" {\n"
                    -"if ("
                    selfRef()
                    -" !== undefined) return "
                    selfRef()
                    -"\nelse {\n"
                    val exp = (typedRule.delegateExpression as KtCallExpression).valueArguments.first()
                        .getArgumentExpression()
                    if (exp is KtLambdaExpression) {
                        val content = exp.bodyExpression?.statements?.singleOrNull()
                        if (content != null && content.actuallyCouldBeExpression) {
                            -"const r = "
                            -content
                            -"\n"
                        } else {
                            -"const r = ("
                            -exp
                            -")()\n"
                        }
                    } else {
                        -"const r = ("
                        -exp
                        -")()\n"
                    }
                    selfRef()
                    -" = r\nreturn r\n"
                    -"}\n"
                    -"}\n"
                }
                if (typedRule.isMember) {
                    -(typedRule.visibilityModifier() ?: "public")
                    -" get "
                    -typedRule.nameIdentifier
                    -"(): "
                    -(typedRule.typeReference ?: typedRule.resolvedVariable?.type) //TODO: Handle unimported type
                    if (typedRule.isLazy) {
                        lazy {
                            -"this._"
                            -typedRule.nameIdentifier
                        }
                    } else {
                        -" { return "
                        -"this._"
                        -typedRule.nameIdentifier
                        -"; }"
                    }
                } else {
                    if (typedRule.isTopLevel && !typedRule.isPrivate()) -"export "
                    -"function get"
                    -typedRule.nameIdentifier?.text?.capitalize()
                    -"(): "
                    -(typedRule.typeReference ?: typedRule.resolvedVariable?.type) //TODO: Handle unimported type
                    if (typedRule.isLazy) {
                        lazy {
                            -"_"
                            -typedRule.nameIdentifier
                        }
                    } else {
                        -" { return "
                        -"_"
                        -typedRule.nameIdentifier
                        -"; }"
                    }
                }
            }
            if (typedRule.isVar) {
                typedRule.setter?.let {
                    -"\n"
                    -it
                } ?: run {
                    -"\n"
                    if (typedRule.isMember) {
                        -(typedRule.visibilityModifier() ?: "public")
                        -" set "
                        -typedRule.nameIdentifier
                        -"(value: "
                        -(typedRule.typeReference
                            ?: typedRule.resolvedVariable?.type) //TODO: Handle unimported type
                        -") { this._"
                        -typedRule.nameIdentifier
                        -" = value; }"
                    } else {
                        if (typedRule.isTopLevel && !typedRule.isPrivate()) -"export "
                        -"function set"
                        -typedRule.nameIdentifier?.text?.capitalize()
                        -"(value: "
                        -(typedRule.typeReference
                            ?: typedRule.resolvedVariable?.type) //TODO: Handle unimported type
                        -") { _"
                        -typedRule.nameIdentifier
                        -" = value; }"
                    }
                }
            }
        }
    }
    //Local with guard return
    handle<KtProperty>(
        condition = {
            typedRule.isLocal && (typedRule.initializer as? KtBinaryExpression)?.let {
                it.operationToken == KtTokens.ELVIS && (it.right is KtReturnExpression || it.right is KtContinueExpression || it.right is KtBreakExpression || it.right is KtThrowExpression)
            } == true
        },
        priority = 50,
        action = {
            if (typedRule.isVar) {
                -"let "
            } else {
                -"const "
            }
            -typedRule.nameIdentifier
            val type = typedRule.typeReference
            type?.let {
                -": "
                -it
                -" | null"
            }
            val elvis = typedRule.initializer as KtBinaryExpression
            -" = "
            -elvis.left
            -"\nif("
            -typedRule.nameIdentifier
            -" === null) { "
            -elvis.right
            -" }"
        }
    )

    //extension getter/setter
    handle<KtPropertyAccessor>(
        condition = { typedRule.isGetter && typedRule.property.receiverTypeReference != null },
        priority = 8,
        action = {
            withReceiverScope(typedRule.property.resolvedProperty!!) { receiverName ->
                val resolved = typedRule.property.resolvedProperty!!
                if (!typedRule.property.isMember) {
                    if (!typedRule.isPrivate()) -"export "
                    -"function "
                }
                -resolved.tsFunctionGetName
                -typedRule.property.typeParameterList
                -"("
                -receiverName
                -": "
                -typedRule.property.receiverTypeReference
                -"): "
                -(typedRule.property.typeReference
                    ?: typedRule.property.resolvedProperty!!.type)
                -" "
                val body = typedRule.bodyExpression
                if (body is KtBlockExpression) {
                    -body
                } else {
                    -"{ return "
                    -body
                    -"; }"
                }
                -"\n"
            }
        }
    )
    handle<KtPropertyAccessor>(
        condition = { typedRule.isSetter && typedRule.property.receiverTypeReference != null },
        priority = 9,
        action = {
            withReceiverScope(typedRule.property.resolvedProperty!!) { receiverName ->
                val resolved = typedRule.property.resolvedProperty!!
                if (!typedRule.property.isMember) {
                    if (!typedRule.isPrivate()) {
                        -"export "
                    }
                    -"function "
                }
                -resolved.tsFunctionSetName
                -typedRule.property.typeParameterList
                -"("
                -receiverName
                -": "
                -typedRule.property.receiverTypeReference
                -", "
                -(typedRule.parameter?.nameIdentifier ?: -"value")
                -": "
                -(typedRule.property.typeReference ?: typedRule.property.resolvedProperty!!.type)
                -") "
                val body = typedRule.bodyExpression
                if (body is KtBlockExpression) {
                    -body
                } else {
                    -"{ return "
                    -body
                    -"; }"
                }
                -"\n"
            }
        }
    )

    //Member getter/setter
    handle<KtPropertyAccessor>(
        condition = { typedRule.isGetter && typedRule.property.isMember },
        priority = 4,
        action = {
            -(typedRule.visibilityModifier() ?: "public")
            -" get "
            -typedRule.property.nameIdentifier
            -"(): "
            -(typedRule.property.typeReference
                ?: typedRule.property.resolvedProperty!!.type)
            -" "
            val body = typedRule.bodyExpression
            if (body is KtBlockExpression) {
                -body
            } else {
                -"{ return "
                -body
                -"; }"
            }
            -"\n"
        }
    )
    handle<KtPropertyAccessor>(
        condition = { typedRule.isSetter && typedRule.property.isMember },
        priority = 3,
        action = {
            -(typedRule.visibilityModifier() ?: "public")
            -" set "
            -typedRule.property.nameIdentifier
            -"("
            -(typedRule.parameter?.nameIdentifier ?: -"value")
            -": "
            -(typedRule.property.typeReference
                ?: typedRule.property.resolvedProperty!!.type)
            -") "
            -typedRule.bodyBlockExpression
            -"\n"
        }
    )

    //Global getter/setter
    handle<KtPropertyAccessor>(
        condition = { typedRule.isGetter },
        priority = 1,
        action = {
            if (!typedRule.isPrivate()) -"export "
            -"function "
            -typedRule.property.resolvedProperty!!.tsFunctionGetName
            -"(): "
            -(typedRule.property.typeReference
                ?: typedRule.property.resolvedProperty!!.type)
            -" "
            val body = typedRule.bodyExpression
            if (body is KtBlockExpression) {
                -body
            } else {
                -"{ return "
                -body
                -"; }"
            }
            -"\n"
        }
    )
    handle<KtPropertyAccessor>(
        condition = { typedRule.isSetter },
        priority = 2,
        action = {
            if (!typedRule.isPrivate()) -"export "
            -"function "
            -typedRule.property.resolvedProperty!!.tsFunctionSetName
            -"("
            -(typedRule.parameter?.nameIdentifier ?: -"value")
            -": "
            -(typedRule.property.typeReference
                ?: typedRule.property.resolvedProperty!!.type)
            -") "
            -typedRule.bodyBlockExpression
            -"\n"
        }
    )

    //'field' access
    handle<VirtualGet>(
        condition = { typedRule.property is SyntheticFieldDescriptor },
        priority = 1_000_000,
        action = {
            val prop = typedRule.nameReferenceExpression.parentOfType<KtPropertyAccessor>()!!
            if (prop.property.isMember) {
                -"this."
            }
            -"_"
            -typedRule.nameReferenceExpression.parentOfType<KtPropertyAccessor>()!!.property.nameIdentifier
        }
    )
    handle<VirtualSet>(
        condition = { typedRule.property is SyntheticFieldDescriptor },
        priority = 1_000_000,
        action = {
            val prop = typedRule.nameReferenceExpression.parentOfType<KtPropertyAccessor>()!!
            if (prop.property.isMember) {
                -"this."
            }
            -"_"
            -typedRule.nameReferenceExpression.parentOfType<KtPropertyAccessor>()!!.property.nameIdentifier
            -" = "
            -typedRule.value
        }
    )

    //Getter usage
    handle<VirtualGet>(
        condition = {
            replacements.getGet(
                typedRule.property as? PropertyDescriptor ?: return@handle false,
                typedRule.receiverType
            ) != null
        },
        priority = 10_000,
        action = {
            val rule = replacements.getGet(typedRule.property as PropertyDescriptor, typedRule.receiverType)!!
            emitTemplate(
                requiresWrapping = typedRule.expr.actuallyCouldBeExpression,
                type = typedRule.expr.resolvedExpressionTypeInfo?.type,
                ensureReceiverNotNull = typedRule.safe,
                template = rule.template,
                dispatchReceiver = typedRule.dispatchReceiver(this@registerVariable),
                extensionReceiver = typedRule.extensionReceiver(this@registerVariable)
            )
        }
    )
    handle<VirtualGet>(
        condition = {
            (typedRule.property as? PropertyDescriptor ?: return@handle false).tsFunctionGetName != null
        },
        priority = 1000,
        action = {
            val prop = typedRule.property as PropertyDescriptor
            nullWrapAction(
                swiftTranslator = this@registerVariable,
                receiver = typedRule.extensionReceiver(this@registerVariable),
                isExpression = typedRule.expr.actuallyCouldBeExpression,
                skip = !typedRule.safe,
                type = typedRule.expr.resolvedExpressionTypeInfo?.type,
                action = { receiver ->
                    val d = typedRule.dispatchReceiver(this@registerVariable)
                    if (d != null) {
                        -d
                        -"."
                    }
                    -out.addImportGetName(typedRule.property, prop.tsFunctionGetName)
                    -'('
                    if (receiver != null) {
                        -receiver
                    }
                    -')'
                }
            )
        }
    )
    handle<VirtualGet> {
        if (typedRule.safe) {
            -"("
        }
        val rec =
            typedRule.extensionReceiver(this@registerVariable) ?: typedRule.dispatchReceiver(this@registerVariable)
        if (rec != null) {
            -rec
            if (typedRule.safe) {
                -"?."
            } else {
                -"."
            }
        }
        -typedRule.nameReferenceExpression.getIdentifier()
        if (typedRule.safe) {
            -" ?? null)"
        }
    }

    fun KtQualifiedExpression.nre(): KtNameReferenceExpression? = (selectorExpression as? KtNameReferenceExpression)
        ?: ((selectorExpression as? KtCallExpression)?.calleeExpression as? KtNameReferenceExpression)
    handle<KtDotQualifiedExpression>(
        condition = { (typedRule.nre()?.resolvedReferenceTarget as? ValueDescriptor) != null },
        priority = 1000,
        action = {
            val nre = typedRule.nre()!!
            val p = nre.resolvedReferenceTarget as ValueDescriptor
            -VirtualGet(
                receiver = typedRule.replacementReceiverExpression,
                nameReferenceExpression = nre,
                property = p,
                receiverType = typedRule.receiverExpression.resolvedExpressionTypeInfo?.type,
                expr = typedRule,
                safe = false
            )
            (typedRule.selectorExpression as? KtCallExpression)?.let {
                -".invoke"
                -ArgumentsList(
                    on = typedRule.resolvedCall!!.candidateDescriptor as FunctionDescriptor,
                    resolvedCall = typedRule.resolvedCall!!
                )
            }
        }
    )
    handle<KtSafeQualifiedExpression>(
        condition = { (typedRule.nre()?.resolvedReferenceTarget as? ValueDescriptor) != null },
        priority = 1000,
        action = {
            val nre = typedRule.nre()!!
            val p = nre.resolvedReferenceTarget as ValueDescriptor
            -VirtualGet(
                receiver = typedRule.replacementReceiverExpression,
                nameReferenceExpression = nre,
                property = p,
                receiverType = typedRule.receiverExpression.resolvedExpressionTypeInfo?.type,
                expr = typedRule,
                safe = true
            )
            (typedRule.selectorExpression as? KtCallExpression)?.let {
                -".invoke"
                -ArgumentsList(
                    on = typedRule.resolvedCall!!.candidateDescriptor as FunctionDescriptor,
                    resolvedCall = typedRule.resolvedCall!!
                )
            }
        }
    )

    handle<KtNameReferenceExpression>(
        condition = { (typedRule.resolvedReferenceTarget as? ValueDescriptor) != null },
        priority = 1000,
        action = {
            val prop = typedRule.resolvedReferenceTarget as ValueDescriptor
            -VirtualGet(
                nameReferenceExpression = typedRule,
                property = prop,
                receiverType = null,
                expr = typedRule,
                safe = false
            )
        }
    )

    //Setter usage
    handle<VirtualSet>(
        condition = {
            (typedRule.property as? PropertyDescriptor ?: return@handle false).tsFunctionSetName != null
        },
        priority = 1000,
        action = {
            val prop = typedRule.property as PropertyDescriptor

            nullWrapAction(
                swiftTranslator = this@registerVariable,
                receiver = typedRule.extensionReceiver(this@registerVariable),
                isExpression = false,
                skip = !typedRule.safe,
                type = typedRule.expr.resolvedExpressionTypeInfo?.type,
                action = { receiver ->
                    if (typedRule.property.dispatchReceiverParameter != null) {
                        -typedRule.dispatchReceiver(this@registerVariable)
                        -"."
                    }
                    -out.addImportGetName(typedRule.property, prop.tsFunctionSetName)
                    -'('
                    if (receiver != null) {
                        -receiver
                        -", "
                    }
                    -typedRule.value
                    -')'
                }
            )
        }
    )
    handle<VirtualSet>(
        condition = {
            replacements.getSet(
                typedRule.property as? PropertyDescriptor ?: return@handle false,
                typedRule.receiverType
            ) != null
        },
        priority = 10_000,
        action = {
            val rule = replacements.getSet(typedRule.property as PropertyDescriptor, typedRule.receiverType)!!
            emitTemplate(
                requiresWrapping = false,
                type = typedRule.expr.resolvedExpressionTypeInfo?.type,
                ensureReceiverNotNull = typedRule.safe,
                template = rule.template,
                extensionReceiver = typedRule.extensionReceiver(this@registerVariable),
                dispatchReceiver = typedRule.dispatchReceiver(this@registerVariable),
                value = typedRule.value
            )
        }
    )
    handle<VirtualSet> {
        nullWrapAction(
            swiftTranslator = this@registerVariable,
            receiver = typedRule.extensionReceiver(this@registerVariable)
                ?: typedRule.dispatchReceiver(this@registerVariable),
            isExpression = false,
            skip = !typedRule.safe,
            type = typedRule.expr.resolvedExpressionTypeInfo?.type,
            action = { rec ->
                if (rec != null) {
                    -rec
                    -"."
                }
                -typedRule.nameReferenceExpression.getIdentifier()
                -" = "
                -typedRule.value
            }
        )
    }
    handle<KtBinaryExpression>(
        condition = {
            val left = typedRule.left as? KtQualifiedExpression ?: return@handle false
            val nre = left.selectorExpression as? KtNameReferenceExpression ?: return@handle false
            val pd = nre.resolvedReferenceTarget as? ValueDescriptor ?: return@handle false
            (typedRule.resolvedVariableReassignment == true || typedRule.operationToken == KtTokens.EQ)
        },
        priority = 100,
        action = {
            val left = (typedRule.left as KtQualifiedExpression)
            val nre = left.selectorExpression as KtNameReferenceExpression
            val leftProp = nre.resolvedReferenceTarget as ValueDescriptor
            val rec: Any = if (typedRule.operationToken == KtTokens.EQ)
                left.replacementReceiverExpression
            else {
                if (left.receiverExpression.isSimple()) {
                    left.replacementReceiverExpression
                } else {
                    val n = "temp${out.uniqueNumber.getAndIncrement()}"
                    -"const $n = "
                    -left.replacementReceiverExpression
                    -";\n"
                    n
                }
            }

            -VirtualSet(
                receiver = rec,
                property = leftProp,
                nameReferenceExpression = nre,
                receiverType = left.receiverExpression.resolvedExpressionTypeInfo?.type,
                expr = typedRule,
                safe = left is KtSafeQualifiedExpression,
                value = if (typedRule.operationToken == KtTokens.EQ) {
                    typedRule.right!!
                } else {
                    ValueOperator(
                        left = VirtualGet(
                            receiver = rec,
                            nameReferenceExpression = nre,
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
            (left.resolvedReferenceTarget as? ValueDescriptor) != null &&
                    (typedRule.resolvedVariableReassignment == true || typedRule.operationToken == KtTokens.EQ)
        },
        priority = 100,
        action = {
            val left = (typedRule.left as KtNameReferenceExpression)
            val leftProp = left.resolvedReferenceTarget as ValueDescriptor
            -VirtualSet(
                property = leftProp,
                nameReferenceExpression = left,
                receiverType = typedRule.left?.resolvedExpressionTypeInfo?.type,
                expr = typedRule,
                safe = false,
                value = if (typedRule.operationToken == KtTokens.EQ) {
                    typedRule.right!!
                } else {
                    ValueOperator(
                        left = VirtualGet(
                            nameReferenceExpression = left,
                            property = leftProp,
                            receiverType = null,
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
}

val PropertyDescriptor.tsFunctionGetName: String?
    get() = annotations.asSequence().find { it.fqName?.asString()?.substringAfterLast('.') == "JsName" }
        ?.allValueArguments
        ?.entries
        ?.firstOrNull()
        ?.value
        ?.value
        ?.toString()?.safeJsIdentifier()
        ?.plus("Get")
        ?: if (this is SyntheticJavaPropertyDescriptor) null else if (extensionReceiverParameter != null) "x" + extensionReceiverParameter!!
            .value
            .type
            .fqNameWithoutTypeArgs
            .split('.')
            .dropWhile { it.firstOrNull()?.isUpperCase() != true }
            .joinToString("") { it.capitalize() } +
                this.name.identifier.capitalize() + "Get"
        else when (this.containingDeclaration) {
            is ClassDescriptor -> null
            is SyntheticClassOrObjectDescriptor -> null
            else -> if (this.accessors.all { it.isDefault } && this.visibility.name == "private") null else "get" + this.name.identifier.capitalize()
        }
val PropertyDescriptor.tsFunctionSetName: String?
    get() = annotations.asSequence().find { it.fqName?.asString()?.substringAfterLast('.') == "JsName" }
        ?.allValueArguments
        ?.entries
        ?.firstOrNull()
        ?.value
        ?.value
        ?.toString()?.safeJsIdentifier()
        ?.plus("Set")
        ?: if (this is SyntheticJavaPropertyDescriptor) null else if (extensionReceiverParameter != null) "x" + extensionReceiverParameter!!
            .value
            .type
            .fqNameWithoutTypeArgs
            .split('.')
            .dropWhile { it.firstOrNull()?.isUpperCase() != true }
            .joinToString("") { it.capitalize() } +
                this.name.identifier.capitalize() + "Set"
        else when (this.containingDeclaration) {
            is ClassDescriptor -> null
            is SyntheticClassOrObjectDescriptor -> null
            else -> if (this.accessors.all { it.isDefault } && this.visibility.name == "private") null else "set" + this.name.identifier.capitalize()
        }

val PropertyDescriptor.tsFunctionGetDefaultName: String?
    get() {
        return "get" + this.name.identifier.capitalize()
    }
val PropertyDescriptor.tsFunctionSetDefaultName: String?
    get() {
        return "set" + this.name.identifier.capitalize()
    }

inline fun <reified T : PsiElement> PsiElement.parentOfType(): T? = parentOfType(T::class.java)
fun <T : PsiElement> PsiElement.parentOfType(type: Class<T>): T? =
    if (type.isInstance(this.parent)) type.cast(this.parent) else this.parent?.parentOfType(type)