package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.utils.camelCase
import com.lightningkite.khrysalis.utils.forEachBetween
import com.lightningkite.khrysalis.utils.forEachBetweenIndexed
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerExpression() {
    handle<KotlinParser.AsOperatorContext> { item ->
        when {
            item.AS() != null -> direct.append("as!")
            item.AS_SAFE() != null -> direct.append("as?")
        }
    }
    handle<KotlinParser.DirectlyAssignableExpressionContext> { item ->
        defaultWrite(item, "")
    }
    handle<KotlinParser.AssignableSuffixContext> { item ->
        defaultWrite(item, "")
    }
    handle<KotlinParser.NavigationSuffixContext> { item ->
        if(item.CLASS() != null){
            direct.append(".self")
        } else {
            defaultWrite(item, "") { child ->
                when (child) {
                    is ParserRuleContext -> true
                    is TerminalNode -> when (child.symbol.type) {
                        KotlinParser.NL -> false
                        else -> true
                    }
                    else -> true
                }
            }
        }
    }
    handle<KotlinParser.InfixFunctionCallContext> { item ->
        var lastIndex = 0
        item.rangeExpression()?.forEachBetweenIndexed(
            forItem = { index, expression ->
                lastIndex = index
                write(item.rangeExpression(index))
            },
            between = {
                if(lastIndex == 0){
                    direct.append(".")
                } else {
                    direct.append(").")
                }
                write(item.simpleIdentifier(lastIndex))
                direct.append('(')
            }
        )
        if(item.rangeExpression().size > 1){
            direct.append(")")
        }
    }
    handle<KotlinParser.PrefixUnaryExpressionContext> { item ->
        defaultWrite(item, "")
    }
    handle<KotlinParser.PrimaryExpressionContext> { item ->
        when(val it = item.simpleIdentifier()?.text) {
            "Unit" -> direct.append("()")
            "this" -> direct.append("self")
            else -> {
                typeReplacements[it]?.let {
                    direct.append(it)
                } ?: run {
                    defaultWrite(item)
                }

            }
        }
    }
    handle<KotlinParser.SafeNavContext> { defaultWrite(it, "") }
    handle<KotlinParser.PostfixUnaryExpressionContext> { item ->

        if(item.primaryExpression().text == "R"){
            val suffixes = item.postfixUnarySuffix()
            val typeSuffix = suffixes.getOrNull(0)?.text?.removePrefix(".")
            val resourceSuffix = suffixes.getOrNull(1)?.text?.removePrefix(".")
            if(typeSuffix != null && resourceSuffix != null) {
                val fixedSuffix = resourceSuffix.camelCase()
                when(typeSuffix){
                    "string" -> direct.append("ResourcesStrings.$fixedSuffix")
                    "color" -> direct.append("ResourcesColors.$fixedSuffix")
                    "drawable" -> direct.append("ResourcesDrawables.$fixedSuffix")
                    else -> throw IllegalArgumentException("Unrecognized suffix $typeSuffix $resourceSuffix ($fixedSuffix)")
                }
                suffixes.drop(2).forEach {
                    write(it)
                }
            }
            return@handle
        }

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

        repeat(safeMemberAccessorCount-1) {
            direct.append('(')
        }
        write(item.primaryExpression())
        var handledCount = 0
        var first = true
        item.postfixUnarySuffix().forEach {
            if(it.navigationSuffix()?.memberAccessOperator()?.safeNav() != null){
                if(first){
                    first = false
                } else {
                    handledCount++
                    if(handledCount < safeMemberAccessorCount) {
                        direct.append(')')
                    }
                }
            }
            write(it)
        }
    }
    handle<KotlinParser.TypeArgumentsContext> {
        direct.append('<')
        it.typeProjection().forEachBetween(
            forItem = { write(it) },
            between = { direct.append(", ") }
        )
        direct.append('>')
    }
    handle<KotlinParser.LabelContext> {  }
}
