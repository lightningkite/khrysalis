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
import org.jetbrains.kotlin.load.java.descriptors.JavaClassConstructorDescriptor
import org.jetbrains.kotlin.resolve.annotations.hasJvmStaticAnnotation
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassNotAny

fun TypescriptTranslator.registerResources() {
    handle<KtDotQualifiedExpression>(
        condition = {
            val target = (typedRule.selectorExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? PropertyDescriptor ?: return@handle false
            target.containingDeclaration.fqNameSafe.asString().endsWith("R.string")
        },
        priority = 199999
    ) {
        val target = (typedRule.selectorExpression as KtNameReferenceExpression).resolvedReferenceTarget as PropertyDescriptor
        -out.addImportGetName(target.containingDeclaration.containingDeclaration!!, "Strings")
        -"."
        -target.name.asString().safeJsIdentifier()
    }
    handle<KtDotQualifiedExpression>(
        condition = {
            val target = (typedRule.selectorExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? PropertyDescriptor ?: return@handle false
            target.containingDeclaration.fqNameSafe.asString().endsWith("R.drawable")
        },
        priority = 199999
    ) {
        val target = (typedRule.selectorExpression as KtNameReferenceExpression).resolvedReferenceTarget as PropertyDescriptor
        -out.addImportGetName(target.containingDeclaration.containingDeclaration!!, "Drawables")
        -"."
        -target.name.asString().safeJsIdentifier()
    }
    handle<KtDotQualifiedExpression>(
        condition = {
            val target = (typedRule.selectorExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? PropertyDescriptor ?: return@handle false
            target.containingDeclaration.fqNameSafe.asString().endsWith("R.color")
        },
        priority = 199999
    ) {
        val target = (typedRule.selectorExpression as KtNameReferenceExpression).resolvedReferenceTarget as PropertyDescriptor
        -out.addImportGetName(target.containingDeclaration.containingDeclaration!!, "Colors")
        -"."
        -target.name.asString().safeJsIdentifier()
    }
    handle<KtDotQualifiedExpression>(
        condition = {
            val target = (typedRule.selectorExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? PropertyDescriptor ?: return@handle false
            target.containingDeclaration.fqNameSafe.asString().endsWith("R.dimen")
        },
        priority = 199999
    ) {
        val target = (typedRule.selectorExpression as KtNameReferenceExpression).resolvedReferenceTarget as PropertyDescriptor
        -out.addImportGetName(target.containingDeclaration.containingDeclaration!!, "Dimen")
        -"."
        -target.name.asString().safeJsIdentifier()
    }
}
