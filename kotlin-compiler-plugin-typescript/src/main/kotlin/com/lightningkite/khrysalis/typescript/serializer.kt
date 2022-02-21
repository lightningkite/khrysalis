package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.abstractions.SafeLetChain
import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import com.lightningkite.khrysalis.analysis.*
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import com.lightningkite.khrysalis.util.satisfies
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassNotAny

fun TypescriptTranslator.registerSerializer() {
    handle<KtDotQualifiedExpression>(
        condition = {
            val call = (typedRule.selectorExpression as? KtCallExpression)?.resolvedCall ?: return@handle false
            val descriptor = call.resultingDescriptor
            if(descriptor.name.asString() != "serializer") return@handle false
            if(descriptor.returnType?.satisfies("KSerializer") != true) return@handle false
            val receiver = typedRule.receiverExpression.resolvedExpressionTypeInfo?.type?.constructor?.declarationDescriptor as? ClassDescriptor ?: return@handle false
            if(!receiver.isCompanionObject) return@handle false
            ((typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.resultingDescriptor as? FunctionDescriptor)?.let {
                it.name.asString() == "serializer"
            } ?: false
        },
        priority = 99999
    ) {
        -'['
        val receiver = typedRule.receiverExpression.resolvedExpressionTypeInfo!!.type!!.constructor.declarationDescriptor as ClassDescriptor
        val desc = receiver.containingDeclaration
        when (desc) {
            is ClassDescriptor -> {
                var current: ClassDescriptor = desc
                val items = arrayListOf(current)
                while (true) {
                    current = current.containingDeclaration as? ClassDescriptor ?: break
                    items += current
                }
                out.addImport(current)
                items.asReversed().forEachBetween(
                    forItem = { -it.name.asString().safeJsIdentifier() },
                    between = { -'.' }
                )
            }
            is TypeParameterDescriptor -> {
                -desc.name.asString().safeJsIdentifier()
            }
            else -> {
                -"/*${desc::class.java}*/"
            }
        }

        val call = (typedRule.selectorExpression as KtCallExpression).resolvedCall!!
        call.valueArgumentsByIndex?.forEach {
            -", "
            -it.arguments.firstOrNull()
        }
        -']'
    }
}
