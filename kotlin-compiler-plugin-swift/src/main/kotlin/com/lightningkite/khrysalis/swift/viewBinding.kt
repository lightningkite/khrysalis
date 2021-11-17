package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.analysis.resolvedCall
import com.lightningkite.khrysalis.analysis.resolvedReferenceTarget
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperClassifiers
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassNotAny


fun SwiftTranslator.registerViewBinding() {
    handle<KtDotQualifiedExpression>(
        condition = {
            ((typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.resultingDescriptor as? FunctionDescriptor)?.let {
                it.name.asString() == "inflate" && it.valueParameters.singleOrNull()?.type?.constructor?.declarationDescriptor?.fqNameOrNull()
                    ?.asString() == "android.view.LayoutInflater"
            } ?: false
        },
        priority = 99999
    ) {
        -typedRule.receiverExpression
        -"()"
    }
    handle<KtDotQualifiedExpression>(
        condition = {
            val property = ((typedRule.selectorExpression as? KtSimpleNameExpression)?.resolvedReferenceTarget as? PropertyDescriptor) ?: return@handle false
            val containingClass = (property.containingDeclaration as? ClassDescriptor) ?: return@handle false
            property.name.asString() == "next" && containingClass.name.asString().endsWith("Binding") && containingClass.getSuperClassNotAny() == null
        },
        priority = 99999
    ) {
        -typedRule.receiverExpression
        -".nextView"
    }
}
