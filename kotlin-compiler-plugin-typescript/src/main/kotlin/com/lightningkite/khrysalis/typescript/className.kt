package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.util.fqNameWithTypeArgs
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import com.lightningkite.khrysalis.analysis.*


val skippedExtensions = setOf(
    "com.lightningkite.butterfly.AnyObject",
    "com.lightningkite.butterfly.AnyHashable",
    "com.lightningkite.butterfly.Hashable",
    "com.lightningkite.butterfly.Equatable",
    "com.lightningkite.butterfly.IsHashable",
    "com.lightningkite.butterfly.IsEquatable",
    "com.lightningkite.butterfly.Codable",
    "com.lightningkite.butterfly.IsCodable",
    "com.lightningkite.butterfly.IsCodableAndEquatable",
    "com.lightningkite.butterfly.IsCodableAndHashable"
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
