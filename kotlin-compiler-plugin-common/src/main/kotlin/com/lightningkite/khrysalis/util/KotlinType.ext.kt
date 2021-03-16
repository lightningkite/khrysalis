package com.lightningkite.khrysalis.util

import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.resolve.calls.inference.CapturedTypeConstructor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.checker.NewCapturedTypeConstructor
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
val KotlinType.fqNameWithoutTypeArgs: String get() =  try {
    getJetTypeFqName(false)
} catch(e: Exception){
    "<err>"
}
val KotlinType.fqNameWithTypeArgs: String get() = try {
    getJetTypeFqName(true)
} catch(e: Exception){
    "<err>"
}
fun KotlinType.cannotSatisfy(stringRequirement: String): Boolean = !satisfies(stringRequirement)
fun KotlinType.satisfies(stringRequirement: String): Boolean {
    if(stringRequirement == "primitive") {
        return this.fqNameWithoutTypeArgs in primitiveTypes
    }
    if(stringRequirement == "array"){
        return this.fqNameWithoutTypeArgs in arrayTypes
    }
    if(this.fqNameWithTypeArgs == stringRequirement) return true
    if(this.fqNameWithoutTypeArgs == stringRequirement) return true
    this.supertypes().forEach {
        if(it.fqNameWithTypeArgs == stringRequirement) return true
        if(it.fqNameWithoutTypeArgs == stringRequirement) return true
    }
    return false
}