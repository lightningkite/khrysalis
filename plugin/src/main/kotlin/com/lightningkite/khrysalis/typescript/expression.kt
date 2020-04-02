package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.swift.TabWriter
import com.lightningkite.khrysalis.utils.camelCase
import com.lightningkite.khrysalis.utils.forEachBetween
import com.lightningkite.khrysalis.utils.forEachBetweenIndexed
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import org.jetbrains.kotlin.KotlinParser

fun TypescriptAltListener.registerExpression() {

    handle<KotlinParser.NavigationSuffixContext> { item ->
        if (item.CLASS() != null) {
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
                if (lastIndex == 0) {
                    direct.append(".")
                } else {
                    direct.append(").")
                }
                write(item.simpleIdentifier(lastIndex))
                direct.append('(')
            }
        )
        if (item.rangeExpression().size > 1) {
            direct.append(")")
        }
    }
    handle<KotlinParser.ValueArgumentsContext> {
        val sampleHasNoLabel = it.valueArgument().firstOrNull()?.MULT() == null
        if (it.valueArgument().any { (it.MULT() == null) != sampleHasNoLabel }) {
            println("WARNING: Function call at line ${it.start.line} has some arguments with keys and some without.  This is not supported by the standard function definition converter.")
        }
        direct.append("(")
        it.valueArgument().forEachBetween(
            forItem = { write(it) },
            between = { direct.append(", ") }
        )
        direct.append(")")
    }
    handle<KotlinParser.ValueArgumentContext> {
        write(it.expression())
    }

    handle<KotlinParser.AsExpressionContext> { ctx ->
        ctx.asOperator().zip(ctx.type()).takeUnless { it.isEmpty() }?.let {
            it.asReversed().forEachIndexed { index, (op, type) ->
                if (op.AS_SAFE() != null) {
                    direct.append("((): ")
                    write(type)
                    direct.append(" | null => { const _item: any = ")
                }
            }
            write(ctx.prefixUnaryExpression())
            it.forEachIndexed { index, (op, type) ->
                if (op.AS_SAFE() != null) {
                    val basicType = type.getBasicType()
                    if (basicType.startsWith('*')) {
                        direct.append("; if(typeof _item == \"${basicType.drop(1)}\") return _item; else return null })()")
                    } else {
                        resolve(basicType).firstOrNull()?.let {
                            direct.append("; if (_item.implementsInterface${it.name}) return _item; else return null })()")
                        } ?: run {
                            direct.append("; if (_item instanceof ")
                            write(type)
                            direct.append(") return _item; else return null })()")
                        }
                    }
                } else {
                    direct.append(" as ")
                    write(type)
                }
            }
        } ?: run {
            write(ctx.prefixUnaryExpression())
        }
    }

    handle<KotlinParser.PostfixUnaryExpressionContext> { item ->

        val primaryExpression = item.primaryExpression()
        val postfixUnarySuffixes = item.postfixUnarySuffix()

        if (primaryExpression.text == "R") {
            val suffixes = postfixUnarySuffixes
            val typeSuffix = suffixes.getOrNull(0)?.text?.removePrefix(".")
            val resourceSuffix = suffixes.getOrNull(1)?.text?.removePrefix(".")
            if (typeSuffix != null && resourceSuffix != null) {
                val fixedSuffix = resourceSuffix.camelCase()
                when (typeSuffix) {
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

        val lastCallSuffix = postfixUnarySuffixes?.lastOrNull()?.callSuffix()
        if (lastCallSuffix != null) {
            val primaryExpressionText = primaryExpression.text.trim()
            val repl = functionReplacements[primaryExpressionText]
            if (repl != null) {
                repl.invoke(this, item)
                return@handle
            }
        }

        // something(Thing.Subthing().modified)
        if (primaryExpression.text.first().isUpperCase()) {
            run breaker@{
                val items = (listOf<ParserRuleContext>(primaryExpression) + postfixUnarySuffixes)
                items.forEachIndexed { index, sub ->
                    (sub as? KotlinParser.PostfixUnarySuffixContext)?.callSuffix()?.let {
                        if(index + 1 >= items.size){
                            direct.append("new ")
                            items.forEach {
                                write(it)
                            }
                        } else {
                            direct.append("(new ")
                            items.subList(0, index + 1).forEach {
                                write(it)
                            }
                            direct.append(")")
                            items.subList(index + 1, items.size).forEach {
                                write(it)
                            }
                        }
                        return@handle
                    }
                    (sub as? KotlinParser.PostfixUnarySuffixContext)?.navigationSuffix()?.let {
                        if (it.simpleIdentifier().text.first().isUpperCase()) {
                            return@forEachIndexed
                        }
                    }
                    (sub as? KotlinParser.PrimaryExpressionContext)?.let { return@forEachIndexed }
                    return@breaker
                }
            }
        }

        write(primaryExpression)
        postfixUnarySuffixes.forEach {
            write(it)
        }
    }

    handle<KotlinParser.RangeExpressionContext> {
        val expressions = it.additiveExpression()
        val parts = expressions.subList(1, expressions.size)
        if(parts.isEmpty()){
            write(expressions.first())
        } else {
            parts.forEach {
                direct.append("makeRange(")
            }
            write(expressions.first())
            parts.reversed().forEach {
                direct.append(", ")
                write(it)
                direct.append(")")
            }
        }
    }
}
