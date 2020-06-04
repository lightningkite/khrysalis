package com.lightningkite.khrysalis.typescript.replacements

import com.lightningkite.khrysalis.util.simpleFqName
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes

data class FunctionReplacement(
    val id: String,
    val infix: Boolean? = null,
    val receiver: String? = null,
    val actualReceiver: String? = null,
    val arguments: List<String>? = null,
    val suppliedArguments: List<String>? = null,
    val hasExplicitTypeArguments: Boolean? = null,
    val comparatorType: String? = null,
    val template: Template
) : ReplacementRule {
    override val priority: Int
        get() = (if (suppliedArguments != null) 20 else 0) + (if (infix != null) 1 else 0) + (if (hasExplicitTypeArguments != null) 16 else 0) + (if (actualReceiver != null) 4 else 0) + (if (receiver != null) 2 else 0) + (if (arguments != null) 4 else 0) + (if (comparatorType != null) 8 else 0)

    fun passes(
        decl: FunctionDescriptor,
        comparatorType: String? = null,
        receiverType: KotlinType?,
        suppliedArguments: Set<String> = setOf()
    ): Boolean {
        if(receiver != null && receiver != decl.extensionReceiverParameter?.type?.getJetTypeFqName(false)) return false
        if (arguments != null) {
            println("${decl.simpleFqName} checking arguments for ${this.arguments} against ${decl.original.valueParameters.joinToString { it.type.getJetTypeFqName(false) }}")
            if(this.arguments.size != decl.original.valueParameters.size) return false
            if (!this.arguments.zip(decl.original.valueParameters)
                    .all { (a, p) -> a == "*" || p.type.getJetTypeFqName(false) == a }
            ) return false
            println("${decl.simpleFqName} Passed!")
        }
        if(this.comparatorType != null && this.comparatorType != comparatorType) return false
        if(actualReceiver != null){
            if(receiverType == null) return false
            val allTypes = listOf(receiverType) + receiverType.supertypes()
//            println("${decl.simpleFqName}, checking for $actualReceiver: ${allTypes.joinToString { it.getJetTypeFqName(false) }}")
            if(allTypes.none { it.getJetTypeFqName(false) == actualReceiver }) return false
        }
        if (this.suppliedArguments != null) {
//            println("${decl.simpleFqName}, checking arguments for ${this.suppliedArguments} against ${suppliedArguments}")
            if(this.suppliedArguments.size != suppliedArguments.size) return false
            if (!this.suppliedArguments.sorted().zip(suppliedArguments.sorted())
                    .all { (a, b) -> a == b }
            ) return false
            println("${decl.simpleFqName} Passed!")
        }
        return true
    }
}