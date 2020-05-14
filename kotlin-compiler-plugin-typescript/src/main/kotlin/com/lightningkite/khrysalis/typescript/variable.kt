package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.typescript.manifest.declaresPrefix
import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.psi.synthetics.SyntheticClassOrObjectDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.isPublishedApi
import java.util.concurrent.atomic.AtomicInteger

val uniqueNumber = AtomicInteger(0)

fun TypescriptTranslator.registerVariable() {

    //If we belong to an interface, skip the implementations
    handle<KtProperty>(
        condition = {
            typedRule.parentOfType<KtClassBody>()?.parentOfType<KtClass>()?.isInterface() == true
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
            val ktClass = typedRule.parentOfType<KtClassBody>()!!.parentOfType<KtClass>()!!
            tr.getter?.let { getter ->
                ktClass.addPostAction {
                    -"\npublic static "
                    -resolved.tsFunctionGetDefaultName
                    (ktClass.typeParameters + tr.typeParameters).takeUnless { it.isEmpty() }?.let {
                        -'<'
                        it.forEachBetween(
                            forItem = { -it },
                            between = { -", " }
                        )
                        -'>'
                    }
                    withReceiverScope(resolved.fqNameSafe.asString()) { r ->
                        -'('
                        -r
                        -": "
                        -tsTopLevelNameElement(ktClass)
                        -')'
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
                ktClass.addPostAction {
                    -"\npublic static "
                    -resolved.tsFunctionSetDefaultName
                    (ktClass.typeParameters + tr.typeParameters).takeUnless { it.isEmpty() }?.let {
                        -'<'
                        it.forEachBetween(
                            forItem = { -it },
                            between = { -", " }
                        )
                        -'>'
                    }
                    withReceiverScope(resolved.fqNameSafe.asString()) { r ->
                        -'('
                        -r
                        -": "
                        -tsTopLevelNameElement(ktClass)
                        -", "
                        -(setter.parameter?.name ?: "value")
                        -": "
                        -tr.typeReference
                        -')'
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
        condition = { typedRule.getter != null && (!typedRule.isVar || typedRule.setter != null) && typedRule.initializer == null },
        priority = 100,
        action = {
            -"$declaresPrefix${typedRule.fqName?.asString()}\n"
            -typedRule.getter!!
            -typedRule.setter
        }
    )

    handle<KtProperty> {
        if (typedRule.isMember) {
            -(typedRule.visibilityModifier() ?: "public")
            -" "
            if (!typedRule.isVar) {
                -"readonly "
            }
        } else {
            if (typedRule.isTopLevel && !typedRule.isPrivate()) {
                -"$declaresPrefix${typedRule.fqName?.asString()}\n"
                -"export "
            }
            if (typedRule.isVar) {
                -"let "
            } else {
                -"const "
            }
        }
        if (typedRule.getter != null || typedRule.setter != null || (!typedRule.isPrivate() && typedRule.isTopLevel)) {
            -"_"
        }
        -typedRule.nameIdentifier
        typedRule.typeReference?.let {
            -": "
            -it
        }
        typedRule.initializer?.let {
            -" = "
            -it
        }
        -";\n"
        if (typedRule.getter != null || typedRule.setter != null || (!typedRule.isPrivate() && typedRule.isTopLevel)) {
            typedRule.getter?.let {
                -it
            } ?: run {
                if(stubMode) return@run
                if (typedRule.isMember) {
                    -(typedRule.visibilityModifier() ?: "public")
                    -" get "
                    -typedRule.nameIdentifier
                    -"(): "
                    -(typedRule.typeReference ?: typedRule.resolvedVariable?.type) //TODO: Handle unimported type
                    -" { return "
                    -"this._"
                    -typedRule.nameIdentifier
                    -"; }\n"
                } else {
                    if (typedRule.isTopLevel && !typedRule.isPrivate()) -"export "
                    -"function get"
                    -typedRule.nameIdentifier?.text?.capitalize()
                    -"(): "
                    -(typedRule.typeReference ?: typedRule.resolvedVariable?.type) //TODO: Handle unimported type
                    -" { return "
                    -"_"
                    -typedRule.nameIdentifier
                    -"; }\n"
                }
            }
            if (typedRule.isVar) {
                typedRule.setter?.let {
                    -it
                } ?: run {
                    if(stubMode) return@run
                    if (typedRule.isMember) {
                        -(typedRule.visibilityModifier() ?: "public")
                        -" set "
                        -typedRule.nameIdentifier
                        -"(value: "
                        -(typedRule.typeReference
                            ?: typedRule.resolvedVariable?.type) //TODO: Handle unimported type
                        -") { this._"
                        -typedRule.nameIdentifier
                        -" = value; }\n"
                    } else {
                        if (typedRule.isTopLevel && !typedRule.isPrivate()) -"export "
                        -"function set"
                        -typedRule.nameIdentifier?.text?.capitalize()
                        -"(value: "
                        -(typedRule.typeReference
                            ?: typedRule.resolvedVariable?.type) //TODO: Handle unimported type
                        -") { _"
                        -typedRule.nameIdentifier
                        -" = value; }\n"
                    }
                }
            }
        }
    }

    //extension getter/setter
    handle<KtPropertyAccessor>(
        condition = { typedRule.isGetter && typedRule.property.receiverTypeReference != null },
        priority = 8,
        action = {
            withReceiverScope(typedRule.property.fqName!!.asString()) { receiverName ->
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
                if(stubMode) {
                    -"{}\n"
                    return@handle
                }
                typedRule.bodyExpression?.let {
                    -"{ return "
                    -it
                    -"; }"
                }
                -typedRule.bodyBlockExpression
                -"\n"
            }
        }
    )
    handle<KtPropertyAccessor>(
        condition = { typedRule.isSetter && typedRule.property.receiverTypeReference != null },
        priority = 9,
        action = {
            withReceiverScope(typedRule.property.fqName!!.asString()) { receiverName ->
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
                if(stubMode) {
                    -"{}\n"
                    return@handle
                }
                -typedRule.bodyBlockExpression
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
            typedRule.bodyExpression?.let {
                -"{ return "
                -it
                -"; }"
            }
            if(stubMode) {
                -"{}\n"
                return@handle
            }
            -typedRule.bodyBlockExpression
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
            if(stubMode) {
                -"{}\n"
                return@handle
            }
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
            -"function get"
            -typedRule.property.nameIdentifier!!.text.capitalize()
            -"(): "
            -(typedRule.property.typeReference
                ?: typedRule.property.resolvedProperty!!.type)
            -" "
            if(stubMode) {
                -"{}\n"
                return@handle
            }
            typedRule.bodyExpression?.let {
                -"{ return "
                -it
                -"; }"
            }
            -typedRule.bodyBlockExpression
            -"\n"
        }
    )
    handle<KtPropertyAccessor>(
        condition = { typedRule.isSetter },
        priority = 2,
        action = {
            if (!typedRule.isPrivate()) -"export "
            -"function set"
            -typedRule.property.nameIdentifier!!.text.capitalize()
            -"("
            -(typedRule.parameter?.nameIdentifier ?: -"value")
            -": "
            -(typedRule.property.typeReference
                ?: typedRule.property.resolvedProperty!!.type)
            -") "
            if(stubMode) {
                -"{}\n"
                return@handle
            }
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
                -"this."
            }
            -"_"
            -typedRule.parentOfType<KtPropertyAccessor>()!!.property.nameIdentifier
        }
    )

    //handle virtual property access
    handle<KtNameReferenceExpression>(
        condition = { (typedRule.resolvedReferenceTarget as? PropertyDescriptor)?.tsFunctionGetName != null },
        priority = 100,
        action = {
            val prop = typedRule.resolvedReferenceTarget as PropertyDescriptor
            out.addImport(prop, prop.tsFunctionGetName)
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
    handle<KtDotQualifiedExpression>(
        condition = { ((typedRule.selectorExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? PropertyDescriptor)?.tsFunctionGetName != null },
        priority = 1000,
        action = {
            val nre = (typedRule.selectorExpression as KtNameReferenceExpression)
            val prop = nre.resolvedReferenceTarget as PropertyDescriptor
            out.addImport(prop, prop.tsFunctionGetName)
            if(prop.dispatchReceiverParameter != null) {
                -nre.getTsReceiver()
                -"."
            }
            -prop.tsFunctionGetName
            -'('
            -typedRule.receiverExpression
            -')'
        }
    )
    handle<KtBinaryExpression>(
        condition = {
            val left = typedRule.left as? KtNameReferenceExpression ?: return@handle false
            (left.resolvedReferenceTarget as? PropertyDescriptor)?.tsFunctionSetName != null &&
                    when (typedRule.operationToken) {
                        KtTokens.EQ -> true
                        KtTokens.PLUSEQ -> true
                        KtTokens.MINUSEQ -> true
                        KtTokens.MULTEQ -> true
                        KtTokens.DIVEQ -> true
                        KtTokens.PERCEQ -> true
                        else -> false
                    }
        },
        priority = 100,
        action = {
            val left = (typedRule.left as KtNameReferenceExpression)
            val leftProp = left.resolvedReferenceTarget as PropertyDescriptor
            out.addImport(leftProp, leftProp.tsFunctionSetName)
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
                    operationToken = typedRule.operationToken
                )
            }
            -')'
        }
    )
    handle<KtBinaryExpression>(
        condition = {
            val left = typedRule.left as? KtDotQualifiedExpression ?: return@handle false
            ((left.selectorExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? PropertyDescriptor)?.tsFunctionGetName != null &&
                    when (typedRule.operationToken) {
                        KtTokens.EQ -> true
                        KtTokens.PLUSEQ -> true
                        KtTokens.MINUSEQ -> true
                        KtTokens.MULTEQ -> true
                        KtTokens.DIVEQ -> true
                        KtTokens.PERCEQ -> true
                        else -> false
                    }
        },
        priority = 1000,
        action = {
            val left = (typedRule.left as KtDotQualifiedExpression)
            val nre = (left.selectorExpression as KtNameReferenceExpression)
            val prop = nre.resolvedReferenceTarget as PropertyDescriptor
            out.addImport(prop, prop.tsFunctionSetName)
            if(prop.dispatchReceiverParameter != null) {
                -nre.getTsReceiver()
                -"."
            }
            -prop.tsFunctionSetName
            -'('
            -left.receiverExpression
            -", "
            if (typedRule.operationToken == KtTokens.EQ) {
                -typedRule.right!!
            } else {
                -ValueOperator(
                    left = typedRule.left!!,
                    right = typedRule.right!!,
                    functionDescriptor = typedRule.operationReference.resolvedReferenceTarget as FunctionDescriptor,
                    dispatchReceiver = typedRule.getTsReceiver(),
                    operationToken = typedRule.operationToken
                )
            }
            -')'
        }
    )

    //Getter actual overrides
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
                requiresWrapping = typedRule.parent is KtBlockExpression,
                template = rule.template,
                receiver = typedRule.getTsReceiver()
            )
        }
    )
    handle<KtDotQualifiedExpression>(
        condition = {
            val nre = (typedRule.selectorExpression as? KtNameReferenceExpression) ?: return@handle false
            val pd = nre.resolvedReferenceTarget as? PropertyDescriptor ?: return@handle false
            replacements.getGet(pd) != null
        },
        priority = 10_000,
        action = {
            val nre = (typedRule.selectorExpression as KtNameReferenceExpression)
            val pd = nre.resolvedReferenceTarget as PropertyDescriptor
            val rule = replacements.getGet(pd)!!
            emitTemplate(
                requiresWrapping = typedRule.parent is KtBlockExpression,
                template = rule.template,
                receiver = typedRule.receiverExpression,
                dispatchReceiver = typedRule.getTsReceiver()
            )
        }
    )

    //Setter actual overrides
    handle<KtBinaryExpression>(
        condition = {
            val left = typedRule.left as? KtDotQualifiedExpression ?: return@handle false
            val pd =
                ((left.selectorExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? PropertyDescriptor)
                    ?: return@handle false
            replacements.getSet(pd) != null
        },
        priority = 10_001,
        action = {
            val left = typedRule.left as KtDotQualifiedExpression
            val nre = (left.selectorExpression as KtNameReferenceExpression)
            val pd = (nre.resolvedReferenceTarget as PropertyDescriptor)
            val rule = replacements.getSet(pd)!!
            emitTemplate(
                requiresWrapping = typedRule.parent is KtBlockExpression,
                template = rule.template,
                receiver = left.receiverExpression,
                dispatchReceiver = nre.getTsReceiver(),
                extensionReceiver = null,
                value = typedRule.right
            )
        }
    )
    handle<KtBinaryExpression>(
        condition = {
            val left = typedRule.left as? KtNameReferenceExpression ?: return@handle false
            val pd = (left.resolvedReferenceTarget as? PropertyDescriptor) ?: return@handle false
            replacements.getSet(pd) != null
        },
        priority = 10_000,
        action = {
            val nre = typedRule.left as KtNameReferenceExpression
            val pd = (nre.resolvedReferenceTarget as PropertyDescriptor)
            val rule = replacements.getSet(pd)!!
            emitTemplate(
                requiresWrapping = typedRule.parent is KtBlockExpression,
                template = rule.template,
                receiver = nre.getTsReceiver(),
                extensionReceiver = null,
                value = typedRule.right
            )
        }
    )
}

val PropertyDescriptor.tsFunctionGetName: String?
    get() = if (extensionReceiverParameter != null) "get" + extensionReceiverParameter!!
        .value
        .type
        .getJetTypeFqName(false)
        .split('.')
        .joinToString("") { it.capitalize() } +
            this.name.identifier.capitalize()
    else when (this.containingDeclaration) {
        is ClassDescriptor -> null
        is SyntheticClassOrObjectDescriptor -> null
        else -> if (this.accessors.all { it.isDefault } && this.visibility.name == "private") null else "get" + this.name.identifier.capitalize()
    }
val PropertyDescriptor.tsFunctionSetName: String?
    get() = if (extensionReceiverParameter != null) "set" + extensionReceiverParameter!!
        .value
        .type
        .getJetTypeFqName(false)
        .split('.')
        .joinToString("") { it.capitalize() } +
            this.name.identifier.capitalize()
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