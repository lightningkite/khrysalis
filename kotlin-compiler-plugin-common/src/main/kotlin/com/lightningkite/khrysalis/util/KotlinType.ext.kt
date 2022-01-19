package com.lightningkite.khrysalis.util

import org.jetbrains.kotlin.builtins.isFunctionTypeOrSubtype
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.resolve.calls.inference.CapturedTypeConstructor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
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
val KotlinType.fqNameWithoutTypeArgs: String get() = this.constructor.declarationDescriptor?.fqNameOrNull()?.asString() ?: "unknown"
val KotlinType.fqNameWithTypeArgs: String get() = fqNameWithoutTypeArgs + if(this.arguments.isNotEmpty())
    this.arguments.joinToString { it.type.fqNameWithTypeArgs }
else ""
val KotlinType.simpleNameWithoutTypeArgs: String get() = this.constructor.declarationDescriptor?.name?.asString() ?: "unknown"
val KotlinType.simpleNameWithTypeArgs: String get() = fqNameWithoutTypeArgs + if(this.arguments.isNotEmpty())
    this.arguments.joinToString { it.type.simpleNameWithTypeArgs }
else ""

fun KotlinType.matchesString(string: String): Boolean {
    val desc = constructor.declarationDescriptor
    val maybeFull = string.contains('.')
    val hasArgs = string.contains('<')
    if(maybeFull) {
        if(hasArgs) {
            if(desc?.fqNameOrNull()?.asString() != string.substringBefore('<')) return false
            string.substringAfter('<').trim().dropLast(1).splitToSequence(',').forEachIndexed { index, s ->
                if(arguments.getOrNull(index)?.type?.satisfies(s) != true) return false
            }
            return true
        } else {
            return desc?.fqNameOrNull()?.asString() == string
        }
    } else {
        if(hasArgs) {
            if(desc?.name?.asString() != string.substringBefore('<')) return false
            string.substringAfter('<').trim().dropLast(1).splitToSequence(',').forEachIndexed { index, s ->
                if(arguments.getOrNull(index)?.type?.satisfies(s) != true) return false
            }
            return true
        } else {
            return desc?.name?.asString() == string
        }
    }
}

fun KotlinType.cannotSatisfy(stringRequirement: String): Boolean = !satisfies(stringRequirement)
fun KotlinType.satisfies(stringRequirement: String): Boolean {
    if(stringRequirement == "*") return true
    if(stringRequirement == "primitive") {
        return this.fqNameWithoutTypeArgs in primitiveTypes
    }
    if(stringRequirement == "array"){
        return this.fqNameWithoutTypeArgs in arrayTypes
    }
    if(stringRequirement == "function"){
        return this.isFunctionTypeOrSubtype
    }
    if(this.matchesString(stringRequirement)) return true
    this.supertypes().forEach {
        if(it.matchesString(stringRequirement)) return true
    }
    return false
}