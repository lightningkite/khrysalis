package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.abstractions.SafeLetChain
import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import com.lightningkite.khrysalis.analysis.*
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassNotAny

fun SwiftTranslator.registerSerializer() {
    handle<KtExpression>(
        condition = {
            typedRule.resolvedExpectedExpressionType?.fqNameWithoutTypeArgs == "kotlinx.serialization.KSerializer"
        },
        hierarchyHeight = 10000,
        priority = 99_999
    ) {
        -typedRule.resolvedExpressionTypeInfo?.type?.arguments?.get(0)?.type
        -".self"
    }
}
