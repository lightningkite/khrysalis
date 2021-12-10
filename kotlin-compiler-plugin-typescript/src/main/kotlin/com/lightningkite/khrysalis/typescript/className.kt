package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.util.fqNameWithTypeArgs
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import com.lightningkite.khrysalis.analysis.*


val skippedExtensions = setOf(
    "com.lightningkite.khrysalis.AnyObject",
    "com.lightningkite.khrysalis.AnyHashable",
    "com.lightningkite.khrysalis.Hashable",
    "com.lightningkite.khrysalis.Equatable",
    "com.lightningkite.khrysalis.IsHashable",
    "com.lightningkite.khrysalis.IsEquatable",
    "com.lightningkite.khrysalis.Codable",
    "com.lightningkite.khrysalis.IsCodable",
    "com.lightningkite.khrysalis.IsCodableAndEquatable",
    "com.lightningkite.khrysalis.IsCodableAndHashable"
//    "com.lightningkite.khrysalis.SomeEnum"
)

fun MemberDescriptor.description(): String {
    return when (this) {
        is FunctionDescriptor -> this.name.identifier + this.valueParameters.joinToString {
            it.type.fqNameWithTypeArgs
        }
        is PropertyDescriptor -> this.name.identifier
        else -> return "???"
    }
}
