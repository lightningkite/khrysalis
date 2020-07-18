package com.lightningkite.khrysalis.typescript.replacements

import com.lightningkite.khrysalis.util.AnalysisExtensions
import com.lightningkite.khrysalis.util.cannotSatisfy
import com.lightningkite.khrysalis.util.satisfies
import com.lightningkite.khrysalis.util.simpleFqName
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression
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
    val template: Template
) : ReplacementRule {
    override val priority: Int
        get() = (if (suppliedArguments != null) 20 else 0) +
                (if (infix != null) 1 else 0) +
                (if (hasExplicitTypeArguments != null) 16 else 0) +
                (if (actualReceiver != null) 4 else 0) +
                (if (receiver != null) 2 else 0) +
                (if (arguments != null) 4 else 0) +
                (if (comparatorType != null) 8 else 0) +
                (if(usedAsExpression != null) 8 else 0) +
                (typeArgumentRequirements?.size?.times(32) ?: 0)

    fun passes(
        analysis: AnalysisExtensions,
        call: ResolvedCall<out CallableDescriptor>,
        descriptor: CallableDescriptor
    ): Boolean {
        val hasExplicitTypeArguments = call.call.typeArgumentList != null
        if (this.hasExplicitTypeArguments != null && this.hasExplicitTypeArguments != hasExplicitTypeArguments) return false
        if (receiver != null && receiver != descriptor.extensionReceiverParameter?.type?.getJetTypeFqName(false)) return false
        if (arguments != null) {
            if (this.arguments.size != descriptor.original.valueParameters.size) return false
            if (!this.arguments.zip(descriptor.original.valueParameters)
                    .all { (a, p) -> a == "*" || p.type.getJetTypeFqName(false) == a || p.name.asString() == a }
            ) return false
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
            if (this.usedAsExpression != with(analysis) { (call.call.callElement as? KtExpression)?.actuallyCouldBeExpression }) {
                return false
            }
        }
        if (this.typeArgumentRequirements != null) {
            for ((key, value) in this.typeArgumentRequirements) {
                if (call.typeArguments[call.candidateDescriptor.typeParameters[key]]?.satisfies(value) != true) return false
            }
        }
        return true
    }
}