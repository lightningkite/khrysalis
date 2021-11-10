package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.analysis.resolvedCall
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperClassifiers


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
}