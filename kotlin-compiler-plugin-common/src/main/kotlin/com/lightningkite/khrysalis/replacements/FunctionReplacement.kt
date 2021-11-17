package com.lightningkite.khrysalis.replacements

import com.fasterxml.jackson.annotation.JsonIgnore
import com.lightningkite.khrysalis.analysis.actuallyCouldBeExpression
import com.lightningkite.khrysalis.util.*
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation
import org.jetbrains.kotlin.resolve.calls.callResolverUtil.isInfixCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
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
    val usedAsExpression: Boolean? = null,
    val typeArgumentRequirements: Map<Int, String>? = null,
    val exactArgumentRequirements: Map<Int, String>? = null,
    override val debug: Boolean = false,
    val resultIsNullable: Boolean? = null,
    val reflectiveName: String? = null,
    val template: Template
) : ReplacementRule {

    @get:JsonIgnore() override val priority: Int
        get() = (if (suppliedArguments != null) 20 else 0) +
                (if (infix != null) 1 else 0) +
                (if (hasExplicitTypeArguments != null) 16 else 0) +
                (if (actualReceiver != null) 4 else 0) +
                (if (receiver != null) 2 else 0) +
                (if (arguments != null) 4 else 0) +
                (if (comparatorType != null) 8 else 0) +
                (if (usedAsExpression != null) 8 else 0) +
                (typeArgumentRequirements?.size?.times(32) ?: 0) +
                (exactArgumentRequirements?.size?.times(32) ?: 0)

    fun passes(
        call: ResolvedCall<out CallableDescriptor>,
        descriptor: CallableDescriptor
    ): Boolean {
        if(debug){
            println("Checking applicability for $id at ${call.call.callElement.getTextWithLocation()}:")
            if(infix != null){
                if(infix != call.call.callElement is KtBinaryExpression) println("Not applicable: infix needs ${infix}, got ${call.call.callElement is KtBinaryExpression}")
            }
            val hasExplicitTypeArguments = call.call.typeArgumentList != null
            if (this.hasExplicitTypeArguments != null && this.hasExplicitTypeArguments != hasExplicitTypeArguments) println("Not applicable: hasExplicitTypeArguments requires ${this.hasExplicitTypeArguments}, got ${hasExplicitTypeArguments}")
            if (receiver != null && receiver != descriptor.extensionReceiverParameter?.type?.fqNameWithoutTypeArgs) println("Not applicable: receiver requires $receiver, got ${descriptor.extensionReceiverParameter?.type?.fqNameWithoutTypeArgs}")
            if (arguments != null) {
                if (this.arguments.size != descriptor.original.valueParameters.size)
                    println("Not applicable: arguments require ${this.arguments}, got ${descriptor.original.valueParameters.joinToString { it.name.asString() + ": " + it.type.fqNameWithoutTypeArgs }}")
                else if (!this.arguments.zip(descriptor.original.valueParameters)
                        .all { (a, p) -> a == "*" || p.type.fqNameWithoutTypeArgs == a || p.name.asString() == a }
                ) {
                    println("Not applicable: arguments require ${this.arguments}, got ${descriptor.original.valueParameters.joinToString { it.name.asString() + ": " + it.type.fqNameWithoutTypeArgs }}")
                }
            }
            if (this.comparatorType != null) {
                val comparatorType =
                    ((call.call.callElement as? KtBinaryExpression)?.operationToken as? KtSingleValueToken)?.value
                if (this.comparatorType != comparatorType) println("Not applicable: comparatorType requires ${this.comparatorType}, got ${comparatorType}")
            }
            if (actualReceiver != null && (call.extensionReceiver?.type ?: call.dispatchReceiver?.type)?.satisfies(
                    actualReceiver
                ) != true
            ) println("Not applicable: actualReceiver requires ${actualReceiver}, got ${(call.extensionReceiver?.type ?: call.dispatchReceiver?.type)?.fqNameWithTypeArgs}")
            if (this.suppliedArguments != null) {
                val suppliedArguments =
                    call.valueArguments.filter { it.value.arguments.isNotEmpty() }.keys.map { it.name.asString() }
                if (this.suppliedArguments.size != suppliedArguments.size)
                    println("Not applicable: suppliedArguments require ${this.arguments}, got ${call.valueArguments.keys.joinToString { it.name.asString() + ": " + it.type.fqNameWithoutTypeArgs }}")
                else if (!this.suppliedArguments.sorted().zip(suppliedArguments.sorted())
                        .all { (a, b) -> a == b }
                ) println("Not applicable: suppliedArguments require ${this.arguments}, got ${call.valueArguments.keys.joinToString { it.name.asString() + ": " + it.type.fqNameWithoutTypeArgs }}")
            }
            if (this.usedAsExpression != null) {
                if (this.usedAsExpression != (call.call.callElement as? KtExpression)?.actuallyCouldBeExpression ) {
                    println("Not applicable: usedAsExpression requires ${usedAsExpression}, got ${(call.call.callElement as? KtExpression)?.actuallyCouldBeExpression}")
                }
            }
            if (this.typeArgumentRequirements != null) {
                for ((key, value) in this.typeArgumentRequirements) {
                    if (call.typeArguments[call.candidateDescriptor.typeParameters[key]]?.satisfies(value) != true)
                        println("Not applicable: typeArgumentRequirements requires ${value}, got ${call.typeArguments[call.candidateDescriptor.typeParameters[key]]?.fqNameWithTypeArgs}")
                }
            }
            if (this.exactArgumentRequirements != null) {
                for ((key, value) in this.exactArgumentRequirements) {
                    if (call.valueArguments[call.candidateDescriptor.valueParameters[key]]?.toString() != value)
                        println("Not applicable: exactArgumentRequirements requires '${value}', got '${call.valueArguments[call.candidateDescriptor.valueParameters[key]]?.toString()}'")
                }
            }
        }
        if(infix != null){
            if(infix != call.call.callElement is KtBinaryExpression) return false
        }
        val hasExplicitTypeArguments = call.call.typeArgumentList != null
        if (this.hasExplicitTypeArguments != null && this.hasExplicitTypeArguments != hasExplicitTypeArguments) return false
        if (receiver != null && receiver != descriptor.extensionReceiverParameter?.type?.fqNameWithoutTypeArgs) return false
        if (arguments != null) {
            if (this.arguments.size != descriptor.original.valueParameters.size) return false
            if (!this.arguments.zip(descriptor.original.valueParameters)
                    .all { (a, p) -> a == "*" || p.type.satisfies(a) || p.name.asString() == a }
            ) {
                return false
            }
        }
        if (this.comparatorType != null) {
            val comparatorType =
                ((call.call.callElement as? KtBinaryExpression)?.operationToken as? KtSingleValueToken)?.value
            if (this.comparatorType != comparatorType) return false
        }
        if (actualReceiver != null && (call.extensionReceiver?.type ?: call.dispatchReceiver?.type)?.satisfies(
                actualReceiver
            ) != true
        ) return false
        if (this.suppliedArguments != null) {
            val suppliedArguments =
                call.valueArguments.filter { it.value.arguments.isNotEmpty() }.keys.map { it.name.asString() }
            if (this.suppliedArguments.size != suppliedArguments.size) return false
            if (!this.suppliedArguments.sorted().zip(suppliedArguments.sorted())
                    .all { (a, b) -> a == b }
            ) return false
        }
        if (this.usedAsExpression != null) {
            if (this.usedAsExpression != (call.call.callElement as? KtExpression)?.actuallyCouldBeExpression ) {
                return false
            }
        }
        if (this.typeArgumentRequirements != null) {
            for ((key, value) in this.typeArgumentRequirements) {
                if (call.typeArguments[call.candidateDescriptor.typeParameters[key]]?.satisfies(value) != true) return false
            }
        }
        if (this.exactArgumentRequirements != null) {
            for ((key, value) in this.exactArgumentRequirements) {
                if (call.valueArguments[call.candidateDescriptor.valueParameters[key]]?.toString() != value) return false
            }
        }
        return true
    }
    fun passes(
        descriptor: CallableDescriptor,
        receiverType: KotlinType?
    ): Boolean {
        if(debug){
            if (receiver != null && receiver != descriptor.extensionReceiverParameter?.type?.fqNameWithoutTypeArgs) println("Not applicable: receiver requires $receiver, got ${descriptor.extensionReceiverParameter?.type?.fqNameWithoutTypeArgs}")
            if (arguments != null) {
                if (this.arguments.size != descriptor.original.valueParameters.size)
                    println("Not applicable: arguments require ${this.arguments}, got ${descriptor.original.valueParameters.joinToString { it.name.asString() + ": " + it.type.fqNameWithoutTypeArgs }}")
                else if (!this.arguments.zip(descriptor.original.valueParameters)
                        .all { (a, p) -> a == "*" || p.type.fqNameWithoutTypeArgs == a || p.name.asString() == a }
                ) {
                    println("Not applicable: arguments require ${this.arguments}, got ${descriptor.original.valueParameters.joinToString { it.name.asString() + ": " + it.type.fqNameWithoutTypeArgs }}")
                }
            }
        }
        if (receiver != null && receiver != descriptor.extensionReceiverParameter?.type?.fqNameWithoutTypeArgs) return false
        if (arguments != null) {
            if (this.arguments.size != descriptor.original.valueParameters.size) return false
            if (!this.arguments.zip(descriptor.original.valueParameters)
                    .all { (a, p) -> a == "*" || p.type.fqNameWithoutTypeArgs == a || p.name.asString() == a }
            ) {
                return false
            }
        }
        if(comparatorType != null) return false
        if (actualReceiver != null && (receiverType)?.satisfies(
                actualReceiver
            ) != true
        ) return false
        if(suppliedArguments != null) return false
        if(usedAsExpression != null) return false
        if(typeArgumentRequirements != null) return false
        if(exactArgumentRequirements != null) return false
        return true
    }
}