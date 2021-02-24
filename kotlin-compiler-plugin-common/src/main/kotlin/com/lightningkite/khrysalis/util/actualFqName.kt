package com.lightningkite.khrysalis.util

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.synthetic.SyntheticJavaPropertyDescriptor

val DeclarationDescriptor.simpleFqName: String get() = when{
    this is SyntheticJavaPropertyDescriptor -> {
        val result = getMethod.containingDeclaration.fqNameSafe.asString() + "." + this.fqNameSafe.asString()
        result
    }
    this is FunctionDescriptor && this.extensionReceiverParameter != null -> this.fqNameSafe.asString() + (this.extensionReceiverParameter?.type?.fqNameWithTypeArgs?.let { ">$it" } ?: "")
    this is PropertyDescriptor && this.extensionReceiverParameter != null -> this.fqNameSafe.asString() + (this.extensionReceiverParameter?.type?.fqNameWithTypeArgs?.let { ">$it" } ?: "")
    else -> this.fqNameSafe.asString().substringBefore(".<")
}
val DeclarationDescriptor.simplerFqName: String get() = this.fqNameSafe.asString()
val DeclarationDescriptor.fqNamesToCheck: Sequence<String> get() = sequenceOf(
    simpleFqName,
    simplerFqName
).plus(arrayTypes.asSequence().mapNotNull { if(this.fqNameSafe.asString().startsWith(it)) "array" + this.fqNameSafe.asString().removePrefix(it) else null })