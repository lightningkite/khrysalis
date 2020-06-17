package com.lightningkite.khrysalis.typescript

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject


val skippedExtensions = setOf(
    "com.lightningkite.khrysalis.AnyObject",
    "com.lightningkite.khrysalis.AnyHashable",
    "com.lightningkite.khrysalis.Hashable",
    "com.lightningkite.khrysalis.Equatable"
//    "com.lightningkite.khrysalis.SomeEnum"
)

fun MemberDescriptor.description(): String {
    return when (this) {
        is FunctionDescriptor -> this.name.identifier + this.valueParameters.joinToString {
            it.type.getJetTypeFqName(
                true
            )
        }
        is PropertyDescriptor -> this.name.identifier
        else -> return "???"
    }
}
