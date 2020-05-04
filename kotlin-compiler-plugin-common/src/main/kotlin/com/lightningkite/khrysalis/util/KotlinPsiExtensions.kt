package com.lightningkite.khrysalis.util

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassNotAny
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassOrAny
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes

fun ClassDescriptor.recursiveChildren(filter: DescriptorKindFilter = DescriptorKindFilter.ALL): Sequence<DeclarationDescriptor> =
    this.unsubstitutedMemberScope.getContributedDescriptors(filter) { true }.asSequence() +
            (this.getSuperClassOrAny().takeUnless { it == this }?.recursiveChildren(filter) ?: sequenceOf()) +
            this.getSuperInterfaces().asSequence().flatMap { it.recursiveChildren(filter) }

fun PropertyDescriptor.allOverridden(): Sequence<PropertyDescriptor> =
    this.overriddenDescriptors.asSequence().flatMap { it.allSuperVersions() }
fun FunctionDescriptor.allOverridden(): Sequence<FunctionDescriptor> =
    this.overriddenDescriptors.asSequence().flatMap { it.allSuperVersions() }

fun PropertyDescriptor.allSuperVersions(): Sequence<PropertyDescriptor> =
    sequenceOf(this) + this.overriddenDescriptors.asSequence().flatMap { it.allSuperVersions() }
fun FunctionDescriptor.allSuperVersions(): Sequence<FunctionDescriptor> =
    sequenceOf(this) + this.overriddenDescriptors.asSequence().flatMap { it.allSuperVersions() }
