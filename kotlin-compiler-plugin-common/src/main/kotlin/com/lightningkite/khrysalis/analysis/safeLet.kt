package com.lightningkite.khrysalis.analysis

import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import com.lightningkite.khrysalis.util.parentIfType
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.TypeUtils


fun KtExpression.isSimple(): Boolean = when (this) {
    is KtNameReferenceExpression -> (this@isSimple.resolvedReferenceTarget as? PropertyDescriptor)?.let {
        it.delegateField == null && it.getter == null && !it.isVar && it.containingDeclaration !is ClassDescriptor
    } ?: (this@isSimple.resolvedReferenceTarget as? VariableDescriptor)?.let {
        !it.isVar && it.containingDeclaration !is ClassDescriptor
    } ?: false
    is KtConstantExpression,
    is KtThisExpression -> true
    else -> false
}

val KtExpression.actuallyCouldBeExpression: Boolean
    get() {
        val exp = this
        if (exp is KtStatementExpression) {
            return false
        }
        if(exp !is KtConstantExpression && exp.resolvedExpressionTypeInfo?.type?.fqNameWithoutTypeArgs == "kotlin.Nothing") return false
        var parentControlBody: KtContainerNodeForControlStructureBody? =
            exp.parent as? KtContainerNodeForControlStructureBody
        (exp.parent as? KtBlockExpression)?.let {
            if (it.statements.lastOrNull() != exp) {
                return false
            }
            (it.parent as? KtFunctionLiteral)?.let {
                return determineMaybeExpressionLambda(it)
            }
            //Check if control is expression
            if(it.parent is KtWhenEntry) return (it.parent!!.parent as KtExpression).actuallyCouldBeExpression
            if(it.parent is KtTryExpression) return (it.parent as KtExpression).actuallyCouldBeExpression
            if(it.parent is KtCatchClause) return (it.parent!!.parent as KtExpression).actuallyCouldBeExpression
            parentControlBody = it.parent as? KtContainerNodeForControlStructureBody ?: return false
        }
        parentControlBody?.let { return determineMaybeExpressionControl(it) }
        return true
    }

private fun determineMaybeExpressionLambda(it: KtFunctionLiteral): Boolean {
    it.resolvedExpectedReturnType?.let { expected ->
        if (expected !is TypeUtils.SpecialType) {
            if(expected.fqNameWithoutTypeArgs in KotlinInfo.dontReturnTypes) {
                return false
            }
        }
    }
    if(it
            .parentIfType<KtLambdaExpression>()
            ?.let {
                it.parentIfType<KtLambdaArgument>() ?: it.parentIfType<KtAnnotatedExpression>()?.parentIfType<KtLambdaArgument>()
            }
            ?.parentIfType<KtCallExpression>()
            ?.let {
                if((it.parent as? KtExpression)?.let {
                        it.isSafeLetDirect() && !it.actuallyCouldBeExpression
                    } == true){
                    return false
                }
                it.parentIfType<KtBinaryExpression>()
                    ?: it.parentIfType<KtQualifiedExpression>()
                        ?.parentIfType<KtBinaryExpression>()
            }
            ?.let { it.isSafeLetChain() && !it.safeLetChainRoot().actuallyCouldBeExpression } == true
    ){
        return false
    }
    return true
}
private fun determineMaybeExpressionControl(it: KtContainerNodeForControlStructureBody): Boolean {
    (it.parent as? KtIfExpression)?.let {
        return it.actuallyCouldBeExpression
    }
    (it.parent as? KtWhenExpression)?.let {
        return it.actuallyCouldBeExpression
    }
    (it.parent as? KtTryExpression)?.let {
        return it.actuallyCouldBeExpression
    }
    return false
}

fun KtExpression.isSafeLetDirect(): Boolean {
    if (this !is KtSafeQualifiedExpression) return false
    val callExpression = this.selectorExpression as? KtCallExpression ?: return false
    if (callExpression.lambdaArguments.isEmpty()) return false
    if ((callExpression.calleeExpression as? KtReferenceExpression)?.resolvedReferenceTarget?.fqNameSafe?.asString() != "kotlin.let") return false
    return true
}

fun KtExpression.isRunDirect(): Boolean {
    if (this !is KtCallExpression) return false
    if (this.lambdaArguments.isEmpty()) return false
    if ((this.calleeExpression as? KtReferenceExpression)?.resolvedReferenceTarget?.fqNameSafe?.asString() != "kotlin.run") return false
    return true
}

fun KtBinaryExpression.isSafeLetChain(): Boolean {
    if (this.operationToken != KtTokens.ELVIS) return false
    if (this.left?.isSafeLetDirect() == true) return true
    return (this.left as? KtBinaryExpression)?.isSafeLetChain() == true
}

fun KtBinaryExpression.safeLetChainRoot(): KtBinaryExpression {
    (this.parent as? KtBinaryExpression)?.let { p ->
        if (p.left == this) {
            if (p.operationToken == KtTokens.ELVIS) {
                return p.safeLetChainRoot()
            }
        }
    }
    return this
}