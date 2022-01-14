package com.lightningkite.khrysalis.typescript

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


fun TypescriptTranslator.registerViewBinding() {
    handle<KtDotQualifiedExpression>(
        condition = {
            ((typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.resultingDescriptor as? FunctionDescriptor)?.let {
                it.name.asString() == "inflate" && it.valueParameters.singleOrNull()?.type?.constructor?.declarationDescriptor?.fqNameOrNull()
                    ?.asString() == "android.view.LayoutInflater"
            } ?: false
        },
        priority = 99999
    ) {
        -typedRule.replacementReceiverExpression
        -".inflate()"
    }
}
