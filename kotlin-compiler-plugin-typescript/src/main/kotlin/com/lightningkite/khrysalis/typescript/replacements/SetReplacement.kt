package com.lightningkite.khrysalis.typescript.replacements

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

data class SetReplacement(
    val id: String,
    val receiver: String? = null,
    val template: Template
) : ReplacementRule {
    override val priority: Int
        get() = (if (receiver != null) 2 else 0)

    fun passes(decl: PropertyDescriptor): Boolean {
        return decl.fqNameSafe.asString() == id &&
                (receiver == null || receiver == decl.extensionReceiverParameter?.type?.getJetTypeFqName(
                    false
                ))
    }
}