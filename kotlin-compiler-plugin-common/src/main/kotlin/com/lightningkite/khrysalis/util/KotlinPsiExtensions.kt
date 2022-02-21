package com.lightningkite.khrysalis.util

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassNotAny
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassOrAny
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.MemberScope
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
fun CallableMemberDescriptor.allOverridden(): Sequence<CallableMemberDescriptor> =
    this.overriddenDescriptors.asSequence().flatMap { it.allSuperVersions() }

fun PropertyDescriptor.allSuperVersions(): Sequence<PropertyDescriptor> =
    sequenceOf(this) + this.overriddenDescriptors.asSequence().flatMap { it.allSuperVersions() }
fun FunctionDescriptor.allSuperVersions(): Sequence<FunctionDescriptor> =
    sequenceOf(this) + this.overriddenDescriptors.asSequence().flatMap { it.allSuperVersions() }
fun CallableMemberDescriptor.allSuperVersions(): Sequence<CallableMemberDescriptor> =
    sequenceOf(this) + this.overriddenDescriptors.asSequence().flatMap { it.allSuperVersions() }


inline fun <reified T> PsiElement.parentIfType(): T? = parent as? T
inline fun <reified T : PsiElement> PsiElement.parentOfType(): T? = parentOfType(T::class.java)
fun <T : PsiElement> PsiElement.parentOfType(type: Class<T>): T? =
    if (type.isInstance(this.parent)) type.cast(this.parent) else this.parent?.parentOfType(type)


fun MemberScope.functionsNamed(name: String) = getContributedFunctions(Name.identifier(name), NoLookupLocation.FROM_BACKEND)


fun KtExpression.hasNewlineBefore(typedRule: KtQualifiedExpression): Boolean {
    return typedRule.prevSibling
        ?.let { it as? PsiWhiteSpace }
        ?.textContains('\n') == true
}