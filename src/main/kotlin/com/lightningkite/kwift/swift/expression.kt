package com.lightningkite.kwift.swift

import org.antlr.v4.runtime.ParserRuleContext
import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerExpression() {
    handle<KotlinParser.AsOperatorContext> { item ->
        when {
            item.AS() != null -> direct.append("as!")
            item.AS_SAFE() != null -> direct.append("as?")
        }
    }
    handle<KotlinParser.AssignableSuffixContext> { item ->
        defaultWrite(item, "")
    }
    handle<KotlinParser.NavigationSuffixContext> { item ->
        defaultWrite(item, "")
    }
    handle<KotlinParser.PrimaryExpressionContext> { item ->
        if(item.simpleIdentifier()?.text == "Unit") direct.append("()")
        if(item.simpleIdentifier()?.text == "this") direct.append("self")
        else defaultWrite(item)
    }
    handle<KotlinParser.SafeNavContext> { defaultWrite(it, "") }
    handle<KotlinParser.PostfixUnaryExpressionContext> { item ->
        val lastCallSuffix = item.postfixUnarySuffix()?.lastOrNull()?.callSuffix()
        if (lastCallSuffix != null) {
            val primaryExpressionText = item.primaryExpression().text.trim()
            val secondToLastSuffix = item.postfixUnarySuffix().let { it.getOrNull(it.lastIndex - 1) }
            if (secondToLastSuffix != null &&
                secondToLastSuffix.navigationSuffix()?.memberAccessOperator()?.safeNav() != null &&
                secondToLastSuffix.navigationSuffix()?.simpleIdentifier()?.text == "let"
            ) {
                handleLet(this, item, secondToLastSuffix)
                return@handle
            }
            val repl = functionReplacements[primaryExpressionText]
            if (repl != null) {
                repl.invoke(this, item)
                return@handle
            }
        }

        val safeMemberAccessorCount = item.postfixUnarySuffix().count { it.navigationSuffix()?.memberAccessOperator()?.safeNav() != null }

        repeat(safeMemberAccessorCount) {
            direct.append('(')
        }
        write(item.primaryExpression())
        item.postfixUnarySuffix().forEach {
            write(it)
            if(it.navigationSuffix()?.memberAccessOperator()?.safeNav() != null){
                direct.append(')')
            }
        }
    }
}
