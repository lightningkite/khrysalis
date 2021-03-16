package com.lightningkite.khrysalis.abstractions

import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.types.KotlinType

data class IfCondition(val expression: KtExpression)

data class ArgumentsList(
    val on: FunctionDescriptor,
    val resolvedCall: ResolvedCall<out CallableDescriptor>,
    val prependArguments: List<Any> = listOf(),
    val replacements: Map<ValueParameterDescriptor, Any?> = mapOf()
) {

}

data class VirtualFunction(
    val name: Any,
    val resolvedFunction: FunctionDescriptor? = null,
    val typeParameters: List<Any>,
    val valueParameters: List<Any>,
    val returnType: Any,
    val body: Any?
)

data class ValueOperator(
    val left: Any,
    val right: Any,
    val functionDescriptor: FunctionDescriptor,
    val dispatchReceiver: Any? = null,
    val operationToken: IElementType,
    val resolvedCall: ResolvedCall<out CallableDescriptor>? = null
)

data class VirtualArrayGet(
    val arrayExpression: Any,
    val indexExpressions: List<Any>,
    val functionDescriptor: FunctionDescriptor,
    val dispatchReceiver: Any? = null,
    val resolvedCall: ResolvedCall<out CallableDescriptor>? = null
)

data class SafeLetChain(
    val outermost: KtExpression,
    val entries: List<Pair<KtExpression, KtLambdaExpression>>,
    val default: KtExpression?
)
data class BasicType(val type: KotlinType)
data class CompleteReflectableType(val type: KotlinType)
data class KtUserTypeBasic(val type: KtUserType)
data class SwiftExtensionStart(
    val forDescriptor: CallableDescriptor,
    val receiver: KtTypeReference?,
    val typeParams: KtTypeParameterList?
)

data class VirtualGet(
    val receiver: Any,
    val property: PropertyDescriptor,
    val receiverType: KotlinType?,
    val expr: KtQualifiedExpression,
    val safe: Boolean
)

data class VirtualSet(
    val receiver: Any,
    val property: PropertyDescriptor,
    val receiverType: KotlinType?,
    val expr: KtExpression,
    val safe: Boolean,
    val value: Any,
    val dispatchReceiver: String?
)

data class ReceiverFor(
    val expression: KtExpression
)