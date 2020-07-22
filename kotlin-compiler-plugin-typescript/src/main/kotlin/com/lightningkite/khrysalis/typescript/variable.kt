package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.typescript.manifest.declaresPrefix
import com.lightningkite.khrysalis.util.forEachBetween
import com.lightningkite.khrysalis.util.simpleFqName
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.psi.synthetics.SyntheticClassOrObjectDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.synthetic.SyntheticJavaPropertyDescriptor
import org.jetbrains.kotlin.types.KotlinType
import java.util.concurrent.atomic.AtomicInteger

val uniqueNumber = AtomicInteger(0)

data class VirtualGet(
    val receiver: Any,
    val property: PropertyDescriptor,
    val receiverType: KotlinType?,
    val expr: KtExpression,
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
            val ktClassBody = typedRule.parentOfType<KtClassBody>()!!
            val ktClass = ktClassBody.parentOfType<KtClass>()!!
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
                    withReceiverScope(resolved) { r ->
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
                    withReceiverScope(resolved) { r ->
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
        condition = { typedRule.getter != null && (!typedRule.isVar || typedRule.setter != null) && typedRule.initializer == null },
        priority = 100,
        action = {
            -"$declaresPrefix${typedRule.simpleFqName}\n"
            -typedRule.getter!!
            -typedRule.setter
        }
    )

    handle<KtProperty> {
        if (typedRule.isMember) {
            -(typedRule.visibilityModifier() ?: "public")
            -" "
            if(typedRule.hasModifier(KtTokens.ABSTRACT_KEYWORD)){
                -"abstract "
            }
            if (!typedRule.isVar) {
                -"readonly "
            }
        } else {
            if (typedRule.isTopLevel && !typedRule.isPrivate()) {
                -"$declaresPrefix${typedRule.simpleFqName}\n"
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
        } ?: run {
            if(typedRule.isMember || typedRule.initializer == null){
                -": "
                -typedRule.resolvedProperty?.type
            }
        }
        if(!typedRule.isMember) {
            typedRule.initializer?.let {
                -" = "
                -it
            }
        }
        -";\n"
        if (typedRule.getter != null || typedRule.setter != null || (!typedRule.isPrivate() && typedRule.isTopLevel)) {
            typedRule.getter?.let {
                -it
            } ?: run {
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
    //Local with guard return
    handle<KtProperty>(
        condition = {
            typedRule.isLocal && (typedRule.initializer as? KtBinaryExpression)?.let {
                it.operationToken == KtTokens.ELVIS && (it.right is KtReturnExpression || it.right is KtContinueExpression || it.right is KtBreakExpression)
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
            -"\nif(!"
            -typedRule.nameIdentifier
            -") { "
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
            -"function get"
            -typedRule.property.nameIdentifier!!.text.capitalize()
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
            -"function set"
            -typedRule.property.nameIdentifier!!.text.capitalize()
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

    //Getter usage
    handle<VirtualGet>(
        condition = {
            replacements.getGet(typedRule.property, typedRule.receiverType) != null
        },
        priority = 10_000,
        action = {
            val rule = replacements.getGet(typedRule.property, typedRule.receiverType)!!
            if (typedRule.safe) {
                -"((_it)=>{\n"
                -"if(_it === null) return null;\nreturn "
            }
            emitTemplate(
                requiresWrapping = typedRule.expr.actuallyCouldBeExpression,
                template = rule.template,
                receiver = (if (typedRule.safe) "_it" else typedRule.receiver),
                dispatchReceiver = typedRule.expr.getTsReceiver()
            )
            if (typedRule.safe) {
                -"\n})("
                -typedRule.receiver
                -')'
            }
        }
    )
    handle<VirtualGet>(
        condition = {
            typedRule.property.tsFunctionGetName != null
        },
        priority = 1000,
        action = {
            out.addImport(typedRule.property, typedRule.property.tsFunctionGetName)
            if (typedRule.safe) {
                -"((_it)=>{\n"
                -"if(_it === null) return null;\nreturn "
            }
            if (typedRule.property.dispatchReceiverParameter != null) {
                -typedRule.expr.getTsReceiver()
                -"."
            }
            -typedRule.property.tsFunctionGetName
            -'('
            -(if (typedRule.safe) "_it" else typedRule.receiver)
            -')'
            if (typedRule.safe) {
                -"\n})("
                -typedRule.receiver
                -')'
            }
        }
    )
    handle<VirtualGet> {
        -typedRule.receiver
        if(typedRule.safe){
            -"?."
        } else {
            -"."
        }
        -typedRule.property.name.asString()
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
            out.addImport(typedRule.property, typedRule.property.tsFunctionSetName)

            val tempName = if (typedRule.safe) {
                val r = typedRule.receiver
                if (r is String || (r as? KtExpression)?.isSimple() == true) {
                    -"if("
                    -r
                    -" !== null) "
                    r
                } else {
                    val n = "temp${uniqueNumber.getAndIncrement()}"
                    -"const $n = "
                    -typedRule.receiver
                    -";\nif($n !== null) "
                    n
                }
            } else ""
            if (typedRule.property.dispatchReceiverParameter != null) {
                -typedRule.dispatchReceiver
                -"."
            }
            -typedRule.property.tsFunctionSetName
            -'('
            -(if (typedRule.safe) tempName else typedRule.receiver)
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
        val r = typedRule.receiver
        val tempName = if (r is String || (r as? KtExpression)?.isSimple() == true) {
            -"if("
            -r
            -" !== null) "
            r
        } else {
            val n = "temp${uniqueNumber.getAndIncrement()}"
            -"const $n = "
            -r
            -";\nif($n !== null) "
            n
        }

        -tempName
        -"."
        -typedRule.property.name.asString()
        -" = "
        -typedRule.value
    }
    handle<VirtualSet> {
        -typedRule.expr.allChildren.map {
            if (it is LeafPsiElement && it.elementType == KtTokens.SAFE_ACCESS)
                "."
            else
                it
        }
    }
    handle<KtBinaryExpression>(
        condition = {
            val left = typedRule.left as? KtQualifiedExpression ?: return@handle false
            val nre = left.selectorExpression as? KtNameReferenceExpression ?: return@handle false
            val pd = nre.resolvedReferenceTarget as? PropertyDescriptor ?: return@handle false
            when(typedRule.operationToken) {
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
                    -"const $n = "
                    -left.receiverExpression
                    -";\n"
                    n
                }
            }

            -VirtualSet(
                receiver = rec,
                property = leftProp,
                receiverType = left.receiverExpression.resolvedExpressionTypeInfo?.type,
                expr = typedRule,
                dispatchReceiver = nre.getTsReceiver(),
                safe = left is KtSafeQualifiedExpression,
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
            replacements.getSet(pd) != null &&
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
    get() = if(this is SyntheticJavaPropertyDescriptor) null else if (extensionReceiverParameter != null) "get" + extensionReceiverParameter!!
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
    get() = if(this is SyntheticJavaPropertyDescriptor) null else if (extensionReceiverParameter != null) "set" + extensionReceiverParameter!!
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