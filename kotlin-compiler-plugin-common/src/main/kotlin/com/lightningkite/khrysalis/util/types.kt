package com.lightningkite.khrysalis.util

import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isBoolean
import org.jetbrains.kotlin.types.typeUtil.isPrimitiveNumberType
import org.jetbrains.kotlin.types.typeUtil.supertypes

private val primitiveTypes = setOf(
    "kotlin.Byte",
    "kotlin.Short",
    "kotlin.Int",
    "kotlin.Long",
    "kotlin.UByte",
    "kotlin.UShort",
    "kotlin.UInt",
    "kotlin.ULong",
    "kotlin.Float",
    "kotlin.Double",
    "kotlin.Char",
    "kotlin.String",
    "kotlin.Boolean"
)
val arrayTypes = listOf(
    "kotlin.ByteArray",
    "kotlin.ShortArray",
    "kotlin.IntArray",
    "kotlin.LongArray",
    "kotlin.FloatArray",
    "kotlin.DoubleArray",
    "kotlin.Array"
)
fun KotlinType.cannotSatisfy(stringRequirement: String): Boolean = !satisfies(stringRequirement)
fun KotlinType.satisfies(stringRequirement: String): Boolean {
    if(stringRequirement == "primitive") {
        return this.getJetTypeFqName(false) in primitiveTypes
    }
    if(stringRequirement == "array"){
        return this.getJetTypeFqName(false) in arrayTypes
    }
    if(this.getJetTypeFqName(true) == stringRequirement) return true
    if(this.getJetTypeFqName(false) == stringRequirement) return true
    this.supertypes().forEach {
        if(it.getJetTypeFqName(true) == stringRequirement) return true
        if(it.getJetTypeFqName(false) == stringRequirement) return true
    }
    return false
}