package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.util.forEachBetween
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import com.lightningkite.khrysalis.util.satisfies
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import com.lightningkite.khrysalis.analysis.*
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtension
import java.util.*

private val KtQualifiedExpression_replacementReceiverExpression = WeakHashMap<KtExpression, Any?>()
var KtQualifiedExpression.replacementReceiverExpression: Any
    get() = KtQualifiedExpression_replacementReceiverExpression[this] ?: this.receiverExpression
    set(value) { KtQualifiedExpression_replacementReceiverExpression[this] = value }

fun TypescriptTranslator.registerCast() {
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
}

private fun CallableMemberDescriptor.mostOriginal(): CallableMemberDescriptor {
    return this.overriddenDescriptors.firstOrNull()?.mostOriginal() ?: this
}