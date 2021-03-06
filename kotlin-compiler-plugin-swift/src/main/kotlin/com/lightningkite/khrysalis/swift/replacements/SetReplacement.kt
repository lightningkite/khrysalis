package com.lightningkite.khrysalis.swift.replacements

import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import com.lightningkite.khrysalis.util.simpleFqName
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes

data class SetReplacement(
    val id: String,
    val receiver: String? = null,
    val actualReceiver: String? = null,
    val template: Template
) : ReplacementRule {
    override val priority: Int
        get() = (if (receiver != null) 2 else 0) + (if (actualReceiver != null) 4 else 0)

    fun passes(decl: PropertyDescriptor, receiverType: KotlinType?): Boolean {
        if(actualReceiver != null){
            if(receiverType == null) return false
            val allTypes = listOf(receiverType) + receiverType.supertypes()
//            println("${decl.simpleFqName}, checking for $actualReceiver: ${allTypes.joinToString { it.fqNameWithoutTypeArgs }}")
            if(allTypes.none { it.fqNameWithoutTypeArgs == actualReceiver }) return false
        }
        return (receiver == null || receiver == decl.extensionReceiverParameter?.type?.fqNameWithoutTypeArgs)
    }
}