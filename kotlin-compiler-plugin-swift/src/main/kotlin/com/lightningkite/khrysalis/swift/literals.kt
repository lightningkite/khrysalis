package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.util.satisfies
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes
import com.lightningkite.khrysalis.analysis.*

fun SwiftTranslator.registerLiterals() {
    handle<KtConstantExpression>(
        condition = { typedRule.text.startsWith("'\\u", true) },
        priority = 1,
        action = {
            -"\"\\u{${typedRule.text.substringAfter('u').substringBefore('\'')}}\""
        }
    )
    handle<KtSimpleNameStringTemplateEntry> {
        val stringWrap = typedRule.expression?.resolvedExpressionTypeInfo?.type?.satisfies("kotlin.String") == true
        -"\\("
        if(stringWrap){
            -"String(kotlin: "
        }
        -typedRule.expression
        if(stringWrap) {
            -")"
        }
        -")"
    }
    handle<KtBlockStringTemplateEntry> {
        val stringWrap = typedRule.expression?.resolvedExpressionTypeInfo?.type?.satisfies("kotlin.String") == true
        -"\\("
        if(stringWrap){
            -"String(kotlin: "
        }
        -typedRule.expression
        if(stringWrap) {
            -")"
        }
        -")"
    }
    handle<KtEscapeStringTemplateEntry> {
        if(typedRule.unescapedValue == "$"){
            -"$"
        } else if(typedRule.text.startsWith("\\u")) {
            -"\\u{${typedRule.text.substringAfter('u')}}"
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
        -"\n\"\"\".trimmingCharacters(in: .whitespaces)"
    }
    handle<KtLiteralStringTemplateEntry>(
        condition = {
            val f = typedRule.firstChild
            f is LeafPsiElement && f.text == "\\"
        },
        priority = 100
    ) {
        -"\\\\"
    }

    handle<KtConstantExpression> {
        when(typedRule.node.elementType){
            KtStubElementTypes.INTEGER_CONSTANT -> -typedRule.text.replace("_", "").removeSuffix("L").removeSuffix("l")
            KtStubElementTypes.CHARACTER_CONSTANT -> {
                -'"'
                -typedRule.text.trim('\'').replace("\"", "\\\"")
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
