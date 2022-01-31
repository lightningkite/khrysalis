package com.lightningkite.khrysalis.util

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.types.KotlinType

val CallableDescriptor.throws: Boolean get() = annotations.any { it.fqName?.asString()?.endsWith(".Throws") == true }
val KotlinType.throws: Boolean get() = annotations.any { it.fqName?.asString()?.endsWith(".Throws") == true }
