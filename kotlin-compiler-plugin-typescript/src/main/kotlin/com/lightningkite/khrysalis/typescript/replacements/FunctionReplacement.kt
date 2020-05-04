package com.lightningkite.khrysalis.typescript.replacements

import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

data class FunctionReplacement(
    val id: String,
    val infix: Boolean? = null,
    val receiver: String? = null,
    val arguments: List<String>? = null,
    val comparatorType: String? = null,
    val template: Template
) : ReplacementRule {
    override val priority: Int
        get() = (if (infix != null) 1 else 0) + (if (receiver != null) 2 else 0) + (if (arguments != null) 4 else 0) + (if(comparatorType != null) 8 else 0)

    fun passes(decl: FunctionDescriptor, comparatorType: String? = null): Boolean {
        return decl.fqNameSafe.asString() == id &&
                (receiver == null || receiver == decl.extensionReceiverParameter?.type?.getJetTypeFqName(
                    false
                )) && (arguments == null || decl.valueParameters.zip(arguments)
            .all { (p, a) -> p.type.getJetTypeFqName(false) == a }) &&
                (this.comparatorType == null || this.comparatorType == comparatorType)
    }
}