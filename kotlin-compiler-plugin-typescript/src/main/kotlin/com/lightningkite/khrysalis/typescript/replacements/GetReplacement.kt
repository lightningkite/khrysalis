package com.lightningkite.khrysalis.typescript.replacements

import com.lightningkite.khrysalis.util.simpleFqName
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType

data class GetReplacement(
    val id: String,
    val receiver: String? = null,
    val actualReceiver: String? = null,
    val template: Template
) : ReplacementRule {
    override val priority: Int
        get() = (if (receiver != null) 2 else 0) + (if (actualReceiver != null) 4 else 0)

    fun passes(decl: PropertyDescriptor, receiverType: KotlinType?): Boolean {
        return (receiver == null || receiver == decl.extensionReceiverParameter?.type?.getJetTypeFqName(
                    false
                )) &&
                (actualReceiver == null || actualReceiver == decl.extensionReceiverParameter?.type?.getJetTypeFqName(
                    false
                ) ?: decl.dispatchReceiverParameter?.type?.getJetTypeFqName(false))
    }
}