package com.lightningkite.khrysalis.util

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.synthetic.SyntheticJavaPropertyDescriptor

val DeclarationDescriptor.simpleFqName: String get() = when(this){
    is SyntheticJavaPropertyDescriptor -> {
        val result = getMethod.containingDeclaration.fqNameSafe.asString() + "." + this.fqNameSafe.asString()
        result
    }
    else -> this.fqNameSafe.asString()
}