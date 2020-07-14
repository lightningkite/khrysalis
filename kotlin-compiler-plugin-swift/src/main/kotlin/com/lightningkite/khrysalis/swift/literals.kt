package com.lightningkite.khrysalis.swift

import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

fun SwiftTranslator.registerLiterals() {
    handle<KtSimpleNameStringTemplateEntry> {
        -"\\("
        -typedRule.expression
        -")"
    }
    handle<KtBlockStringTemplateEntry> {
        -"\\("
        -typedRule.expression
        -")"
    }
    handle<KtEscapeStringTemplateEntry> {
        if(typedRule.unescapedValue == "$"){
            -"$"
        } else {
            doSuper()
        }
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
