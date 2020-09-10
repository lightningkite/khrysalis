package com.lightningkite.khrysalis.swift

import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

fun SwiftTranslator.registerLiterals() {
    handle<KtSimpleNameStringTemplateEntry> {
        val markedNull = typedRule.expression?.resolvedExpressionTypeInfo?.type?.isMarkedNullable == true
        -"\\("
        if(markedNull){
            -"("
        }
        -typedRule.expression
        if(markedNull) {
            -").toString()"
        }
        -")"
    }
    handle<KtBlockStringTemplateEntry> {
        val markedNull = typedRule.expression?.resolvedExpressionTypeInfo?.type?.isMarkedNullable == true
        -"\\("
        if(markedNull){
            -"("
        }
        -typedRule.expression
        if(markedNull) {
            -").toString()"
        }
        -")"
    }
    handle<KtEscapeStringTemplateEntry> {
        if(typedRule.unescapedValue == "$"){
            -"$"
        } else {
            doSuper()
        }
    }
    handle<KtStringTemplateExpression>(
        condition = {
            val f = typedRule.firstChild
            f is LeafPsiElement && f.textLength == 3
        },
        priority = 100
    ) {
        -"\"\"\"\n"
        -typedRule.entries
        -"\n\"\"\""
    }

    handle<KtConstantExpression> {
        when(typedRule.node.elementType){
            KtStubElementTypes.INTEGER_CONSTANT -> -typedRule.text.replace("_", "").removeSuffix("L").removeSuffix("l")
            KtStubElementTypes.CHARACTER_CONSTANT -> {
                -'"'
                -typedRule.text.trim('\'')
                -'"'
            }
            KtStubElementTypes.FLOAT_CONSTANT -> {
                if(typedRule.text.startsWith('.')){
                    -'0'
                }
                -typedRule.text.removeSuffix("F").removeSuffix("f")
            }
            else -> doSuper()
        }
    }

    handle<LeafPsiElement>(
        condition = { typedRule.elementType === KtTokens.NULL_KEYWORD },
        priority = 1,
        action = {
            -"nil"
        }
    )
}
