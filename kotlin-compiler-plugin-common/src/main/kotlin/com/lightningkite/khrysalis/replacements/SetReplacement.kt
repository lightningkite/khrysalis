package com.lightningkite.khrysalis.replacements

import com.fasterxml.jackson.annotation.JsonIgnore
import com.lightningkite.khrysalis.util.fqNameWithTypeArgs
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import com.lightningkite.khrysalis.util.satisfies
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
    override val debug: Boolean = false,
    val template: Template
) : ReplacementRule {
    @get:JsonIgnore() override val priority: Int
        get() = (if (receiver != null) 2 else 0) + (if (actualReceiver != null) 4 else 0)

    fun passes(decl: PropertyDescriptor, receiverType: KotlinType?): Boolean {
        if(debug){
            println("Checking applicability for $id:")
            if(actualReceiver != null){
                if(receiverType == null) return false
                if(!receiverType.satisfies(actualReceiver)) {
                    println("Not applicable: actualReceiver needs ${actualReceiver}, got ${receiverType.fqNameWithTypeArgs}")
                }
            }
            if(!(receiver == null || receiver == decl.extensionReceiverParameter?.type?.fqNameWithoutTypeArgs)) {
                println("Not applicable: receiver needs ${receiver}, got ${decl.extensionReceiverParameter?.type?.fqNameWithoutTypeArgs}")
            }
        }
        if(actualReceiver != null){
            if(receiverType == null) return false
            if(!receiverType.satisfies(actualReceiver)) { return false }
        }
        return (receiver == null || receiver == decl.extensionReceiverParameter?.type?.fqNameWithoutTypeArgs)
    }
}