package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.util.forEachBetween
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import com.lightningkite.khrysalis.util.satisfies
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import com.lightningkite.khrysalis.analysis.*
import com.lightningkite.khrysalis.util.parentIfType
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtension
import org.jetbrains.kotlin.types.typeUtil.isInterface
import java.util.*

private val KtQualifiedExpression_replacementReceiverExpression = WeakHashMap<KtExpression, Any?>()
var KtQualifiedExpression.replacementReceiverExpression: Any
    get() = KtQualifiedExpression_replacementReceiverExpression[this] ?: this.receiverExpression
    set(value) { KtQualifiedExpression_replacementReceiverExpression[this] = value }

fun TypescriptTranslator.registerCast() {
//    handle<KtExpression>(
//        hierarchyHeight = Int.MAX_VALUE,
//        priority = 2_000_003
//    ) {
//        -"/*Expected ${typedRule.resolvedExpectedExpressionType}, got ${typedRule.resolvedExpressionTypeInfo?.type}*/"
//        doSuper()
//    }
    handle<KtExpression>(
        condition = {
            val parent = typedRule.parentIfType<KtQualifiedExpression>() ?: return@handle false
            if(parent.receiverExpression != typedRule) return@handle false
            val expectedType = parent.resolvedCall?.resultingDescriptor?.extensionReceiverParameter?.type ?: return@handle false
            replacements.getImplicitCast(
                typedRule.resolvedExpressionTypeInfo?.type ?: return@handle false,
                expectedType
            ) != null
        },
        hierarchyHeight = Int.MAX_VALUE,
        priority = 2_000_002
    ) {
        val parent = typedRule.parentIfType<KtQualifiedExpression>()!!
        val expectedType = parent.resolvedCall!!.resultingDescriptor.extensionReceiverParameter!!.type
        val cast = replacements.getImplicitCast(
            typedRule.resolvedExpressionTypeInfo?.type!!,
            expectedType
        )!!
        emitTemplate(
            template = cast.template,
            receiver = { doSuper() }
        )
    }
    handle<KtExpression>(
        condition = {
            replacements.getImplicitCast(
                typedRule.resolvedExpressionTypeInfo?.type ?: return@handle false,
                typedRule.resolvedExpectedExpressionType ?: return@handle false
            ) != null
        },
        hierarchyHeight = Int.MAX_VALUE,
        priority = 2_000_000
    ) {
        val cast = replacements.getImplicitCast(
            typedRule.resolvedExpressionTypeInfo?.type!!,
            typedRule.resolvedExpectedExpressionType!!
        )!!
        emitTemplate(
            template = cast.template,
            receiver = { doSuper() }
        )
    }
    handle<KtQualifiedExpression>(
        condition = {
            val realType = typedRule.receiverExpression.resolvedExpressionTypeInfo?.type ?: return@handle false
            val targetDescriptor = (typedRule.selectorExpression as? KtReferenceExpression)?.resolvedReferenceTarget as? CallableMemberDescriptor
                ?: return@handle false
            if(targetDescriptor.isExtension) return@handle false
            val originalDescriptor = targetDescriptor.mostOriginal()
            if(targetDescriptor == originalDescriptor) return@handle false
            replacements.getImplicitCast(
                realType,
                originalDescriptor.dispatchReceiverParameter?.type ?: return@handle false
            ) != null
        },
        hierarchyHeight = Int.MAX_VALUE,
        priority = 2_000_001
    ) {
        val realType = typedRule.receiverExpression.resolvedExpressionTypeInfo!!.type!!
        val targetDescriptor = (typedRule.selectorExpression as KtReferenceExpression).resolvedReferenceTarget as CallableMemberDescriptor
        val originalDescriptor = targetDescriptor.mostOriginal()
        val cast = replacements.getImplicitCast(
            realType,
            originalDescriptor.dispatchReceiverParameter?.type!!
        )!!
        typedRule.replacementReceiverExpression = { ->
            emitTemplate(
                template = cast.template,
                receiver = typedRule.receiverExpression
            )
            Unit
        }
        doSuper()
    }
    handle<KtQualifiedExpression> {
        typedRule.allChildren.forEach {
            if(it == typedRule.receiverExpression) -typedRule.replacementReceiverExpression
            else -it
        }
    }

    handle<KtBinaryExpressionWithTypeRHS>(
        condition = { typedRule.operationReference.getReferencedNameElementType() == KtTokens.AS_SAFE },
        priority = 100,
        action = {
            val resolvedType = typedRule.right!!.resolvedType!!

            when {
                resolvedType.isInterface() -> {
                    out.addImport("@lightningkite/khrysalis-runtime", "tryCastInterface")
                    -"tryCastInterface<"
                    -typedRule.right
                    -">("
                    -typedRule.left
                    -", \""
                    -resolvedType.fqNameWithoutTypeArgs.substringAfterLast('.')
                    -"\")"
                }
                resolvedType.isPrimitive() -> {
                    out.addImport("@lightningkite/khrysalis-runtime", "tryCastPrimitive")
                    -"tryCastPrimitive<"
                    -typedRule.right
                    -">("
                    -typedRule.left
                    -", \""
                    -resolvedType
                    -"\")"
                }
                else -> {
                    out.addImport("@lightningkite/khrysalis-runtime", "tryCastClass")
                    -"tryCastClass<"
                    -typedRule.right
                    -">("
                    -typedRule.left
                    -", "
                    -BasicType(resolvedType)
                    -")"
                }
            }
        }
    )

    handle<KtBinaryExpressionWithTypeRHS>(
        condition = {
            replacements.getExplicitCast(
                typedRule.left.resolvedExpressionTypeInfo?.type ?: return@handle false,
                typedRule.right?.resolvedType ?: return@handle false
            ) != null && typedRule.operationReference.getReferencedNameElementType() == KtTokens.AS_KEYWORD
        },
        priority = 101,
        action = {
            val cast = replacements.getImplicitCast(
                typedRule.left.resolvedExpressionTypeInfo?.type!!,
                typedRule.right!!.resolvedType!!
            )!!
            emitTemplate(
                template = cast.template,
                receiver = { doSuper() }
            )
        }
    )
}

private fun CallableMemberDescriptor.mostOriginal(): CallableMemberDescriptor {
    return this.overriddenDescriptors.firstOrNull()?.mostOriginal() ?: this
}