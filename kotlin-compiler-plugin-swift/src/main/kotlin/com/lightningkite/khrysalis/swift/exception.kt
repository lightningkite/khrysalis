package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.types.isNullable

private val useOptionalThrowsTL = ThreadLocal<Boolean>()
var useOptionalThrows: Boolean
    get() = useOptionalThrowsTL.get() ?: false
    set(value){
        useOptionalThrowsTL.set(value)
    }

fun SwiftTranslator.registerException() {
    handle<KtTryExpression>(
        condition = {
            typedRule.actuallyCouldBeExpression &&
                    typedRule.tryBlock.statements.singleOrNull()?.let{ contents ->
                            (contents as? KtQualifiedExpression)?.selectorExpression as? KtCallExpression ?:
                            contents as? KtCallExpression
                    }?.let { it.resolvedCall?.candidateDescriptor?.annotations?.hasAnnotation(FqName("kotlin.jvm.Throws")) } == true &&
                    (typedRule.catchClauses.singleOrNull()?.catchBody as? KtBlockExpression)?.statements?.singleOrNull().let {
                        it is KtConstantExpression && it.text == "null"
                    }
        },
        priority = 10,
        action = {
            -"("
            useOptionalThrows = true
            -typedRule.tryBlock.statements.single()
            useOptionalThrows = false
            -")"
        }
    )
    handle<KtTryExpression>(
        condition = {typedRule.actuallyCouldBeExpression},
        priority = 1,
        action = {
            runWithTypeHeader(typedRule)
            doSuper()
            -" }"
        }
    )
    handle<KtCatchClause> {
        -"catch "
        if(typedRule.catchParameter != null && typedRule.catchParameter?.typeReference?.resolvedType?.fqNameWithoutTypeArgs !in setOf("java.lang.Throwable", "kotlin.Throwable")) {
            -"let "
            -typedRule.catchParameter?.nameIdentifier
            -" as "
            -typedRule.catchParameter?.typeReference
            typedRule.catchParameter?.typeReference?.resolvedType?.let { replacements.getType(it) }?.errorCondition?.let {
                -" where "
                emitTemplate(it, receiver = typedRule.catchParameter?.nameIdentifier)
            }
        } else {
            -"let "
            -typedRule.catchParameter?.nameIdentifier
        }
        -" "
        -typedRule.catchBody
    }
    handle<LeafPsiElement>(
        condition = { typedRule.elementType == KtTokens.TRY_KEYWORD },
        priority = 10,
        action = {
            -"do"
        }
    )
}
